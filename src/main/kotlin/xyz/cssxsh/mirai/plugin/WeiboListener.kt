package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import net.mamoe.mirai.console.util.CoroutineScopeUtils.childScope
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.weibo.data.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.api.*
import java.time.Duration
import java.time.LocalTime

abstract class WeiboListener(val type: String) : CoroutineScope by WeiboHelperPlugin.childScope("WeiboListener-$type") {

    abstract val load: suspend (id: Long) -> List<MicroBlog>

    protected val filter: WeiboFilter get() = WeiboHelperSettings

    protected abstract val tasks: MutableMap<Long, WeiboTaskInfo>

    private val taskJobs = mutableMapOf<Long, Job>()

    private fun taskContactInfos(id: Long) = tasks[id]?.contacts.orEmpty()

    private fun json(id: Long) = data.resolve(type).resolve("$id.json").apply { parentFile.mkdirs() }

    fun start(): Unit = synchronized(taskJobs) {
        tasks.forEach { (uid, _) ->
            taskJobs[uid] = addListener(uid)
        }
    }

    fun stop(): Unit = synchronized(taskJobs) {
        coroutineContext.cancelChildren()
        taskJobs.clear()
    }

    private suspend fun sendMessageToTaskContacts(
        id: Long,
        block: suspend (contact: Contact) -> Message
    ) = taskContactInfos(id).forEach { delegate ->
        runCatching {
            requireNotNull(findContact(delegate)) { "找不到用户" }.let { contact ->
                contact.sendMessage(block(contact))
            }
        }.onFailure {
            logger.warning({ "对[${delegate}]构建消息失败" }, it)
        }
    }

    private operator fun LocalTime.minus(other: LocalTime): Duration =
        Duration.ofSeconds((toSecondOfDay() - other.toSecondOfDay()).toLong())

    private fun List<MicroBlog>.near(time: LocalTime = LocalTime.now()): Boolean {
        return map { it.created.toLocalTime() - time }.any { it.abs() < IntervalSlow }
    }

    private fun addListener(id: Long): Job = launch(SupervisorJob()) {
        logger.info { "添加对$type(${tasks.getValue(id).name}#${id})的监听任务" }
        while (isActive && taskContactInfos(id).isNotEmpty()) {
            val old = runCatching {
                WeiboClient.Json.decodeFromString<List<MicroBlog>>(json(id).readText())
            }.getOrElse {
                emptyList()
            }
            delay((if (old.near()) IntervalSlow else IntervalFast).toMillis())
            runCatching {
                val list = load(id).sortedBy { it.id }
                json(id).writeText(WeiboClient.Json.encodeToString(list))
                list.forEach { blog ->
                    if (blog.created > tasks.getValue(id).last) {
                        sendMessageToTaskContacts(id) { contact ->
                            blog.toMessage(contact)
                        }
                    }
                }

                list.maxByOrNull { it.created }?.let { blog ->
                    logger.verbose { "$type(${id})[${blog.username}]最新微博时间为<${blog.created}>" }
                    tasks.compute(id) { _, info ->
                        info?.copy(last = blog.created)
                    }
                }
            }.onSuccess {
                logger.info { "$type(${id}): ${tasks[id]}监听任务完成一次, 即将进入延时" }
            }.onFailure {
                if (client.info.uid != 0L) {
                    logger.warning { "$type(${id})监听任务执行失败, ${it}，尝试重新加载Cookie" }
                    runCatching {
                        client.restore()
                    }.onSuccess {
                        logger.info { "登陆成功, $it" }
                    }.onFailure { cause ->
                        if ("login" in cause.message.orEmpty()) {
                            LoginContact?.sendMessage("WEIBO登陆状态失效，需要重新登陆")
                        }
                    }
                } else {
                    LoginContact?.sendMessage("WEIBO登陆状态失效，需要重新登陆")
                    logger.warning { "$type(${id})监听任务执行失败, ${it}，" }
                }
            }
        }
    }

    fun addTask(id: Long, name: String, subject: Contact): Unit = synchronized(tasks) {
        tasks.compute(id) { _, info ->
            (info ?: WeiboTaskInfo(name = name)).run {
                copy(contacts = contacts + subject.delegate)
            }
        }
        taskJobs.compute(id) { _, job ->
            job?.takeIf { it.isActive } ?: addListener(id)
        }
    }

    fun removeTask(id: Long, subject: Contact): Unit = synchronized(tasks) {
        tasks.compute(id) { _, info ->
            info?.run {
                copy(contacts = contacts - subject.delegate)
            }
        }
        if (taskContactInfos(id).isEmpty()) {
            tasks.remove(id)
            taskJobs.remove(id)?.cancel()
        }
    }

    fun detail(subject: Contact): String {
        return buildString {
            appendLine("# 订阅列表")
            appendLine("|     NAME     |     ID     |     LAST     |")
            appendLine("|--------------|------------|--------------|")
            tasks.forEach { (id, info) ->
                if (subject.delegate !in info.contacts) return@forEach
                appendLine("| ${info.name} | $id | ${info.last} |")
            }
        }
    }
}