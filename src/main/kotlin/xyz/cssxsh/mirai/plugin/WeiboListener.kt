package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.weibo.data.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.api.flush
import java.time.LocalTime
import kotlin.coroutines.CoroutineContext
import kotlin.math.abs
import kotlin.time.seconds

abstract class WeiboListener: CoroutineScope {

    abstract val type: String

    abstract val load: suspend (id: Long) -> List<MicroBlog>

    override val coroutineContext: CoroutineContext by lazy { CoroutineName("WeiboListener-$type") }

    protected abstract val tasks : MutableMap<Long, WeiboTaskInfo>

    private val taskJobs = mutableMapOf<Long, Job>()

    private fun taskContactInfos(id: Long) = tasks[id]?.contacts.orEmpty()

    private fun json(id: Long) = data.resolve(type).resolve("$id.json").apply { parentFile.mkdirs() }

    fun start(): Unit = synchronized(taskJobs) {
        tasks.forEach { (uid, _) ->
            taskJobs[uid] = addListener(uid)
        }
    }

    fun stop(): Unit = synchronized(taskJobs) {
        taskJobs.forEach { (_, job) ->
            job.cancel()
        }
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

    private fun List<MicroBlog>.near(time: LocalTime = LocalTime.now()): Boolean {
        return mapNotNull { it.createdAt.toLocalTime() }.any { abs(it.toSecondOfDay() - time.toSecondOfDay()).seconds < IntervalSlow }
    }

    private fun addListener(id: Long): Job = launch {
        logger.info { "添加对$type(${tasks.getValue(id).name}#${id})的监听任务" }
        while (isActive && taskContactInfos(id).isNotEmpty()) {
            val old = runCatching {
                WeiboClient.json.decodeFromString<List<MicroBlog>>(json(id).readText())
            }.getOrElse {
                emptyList()
            }
            delay(if (old.near()) IntervalSlow else IntervalFast)
            runCatching {
                val list = load(id).sortedBy { it.id }
                json(id).writeText(WeiboClient.json.encodeToString(list))
                list.forEach { blog ->
                    if (blog.createdAt > tasks.getValue(id).last) {
                        sendMessageToTaskContacts(id) { contact ->
                            blog.buildMessage(contact)
                        }
                    }
                }

                list.maxByOrNull { it.createdAt }?.let { blog ->
                    logger.verbose { "$type(${id})[${blog.username}]最新微博时间为<${blog.createdAt}>" }
                    tasks.compute(id) { _, info ->
                        info?.copy(last = blog.createdAt)
                    }
                }
            }.onSuccess {
                logger.info { "$type(${id}): ${tasks[id]}监听任务完成一次, 即将进入延时" }
            }.onFailure {
                if (client.token.isNotBlank()) {
                    logger.warning { "$type(${id})监听任务执行失败, ${it.message}，尝试重新加载Cookie" }
                    runCatching {
                        client.flush()
                    }.onSuccess {
                        logger.info { "登陆成功, $it" }
                    }
                } else {
                    logger.warning { "$type(${id})监听任务执行失败, ${it.message}，" }
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
            taskJobs.remove(id)?.cancel()
        }
    }
}