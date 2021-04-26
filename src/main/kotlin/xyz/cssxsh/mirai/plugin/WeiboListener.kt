package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.mirai.plugin.WeiboHelperPlugin.logger
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.SimpleMicroBlog
import kotlin.coroutines.CoroutineContext

abstract class WeiboListener: CoroutineScope {

    abstract val type: String

    abstract val load: suspend (Long) -> List<SimpleMicroBlog>

    override val coroutineContext: CoroutineContext by lazy {
        CoroutineName("WeiboListener-$type")
    }

    protected abstract val tasks : MutableMap<Long, WeiboTaskInfo>

    private val taskJobs = mutableMapOf<Long, Job>()

    private fun taskContactInfos(id: Long) = tasks[id]?.contacts.orEmpty()

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
    ) = taskContactInfos(id).forEach { info ->
        runCatching {
            info.getContact().let { contact ->
                contact.sendMessage(block(contact))
            }
        }.onFailure {
            logger.warning({ "对[${info}]构建消息失败" }, it)
        }
    }

    private fun addListener(id: Long): Job = launch {
        logger.info { "添加对$type(${tasks.getValue(id).name}#${id})的监听任务" }
        delay(tasks.getValue(id).interval.random())
        while (isActive && taskContactInfos(id).isNotEmpty()) {
            runCatching {
                load(id).sortedBy { it.id }.onEach { blog ->
                    if (blog.createdAt > tasks.getValue(id).last) {
                        sendMessageToTaskContacts(id) { contact ->
                            blog.buildMessage(contact)
                        }
                    }
                }.maxByOrNull { it.createdAt }?.let { blog ->
                    logger.verbose { "$type(${id})[${blog.username}]最新微博时间为<${blog.createdAt}>" }
                    tasks.compute(id) { _, info ->
                        info?.copy(last = blog.createdAt)
                    }
                }
            }.onSuccess {
                delay(tasks.getValue(id).interval.random().also {
                    logger.info { "$type(${id}): ${tasks[id]}监听任务完成一次, 即将进入延时delay(${it}ms)。" }
                })
            }.onFailure {
                logger.warning({ "$type(${id})监听任务执行失败，尝试重新加载Cookie" }, it)
                WeiboHelperPlugin.runCatching {
                    WeiboHelperSettings.reload()
                    client.loadCookies(WeiboHelperSettings.initCookies)
                    client.login()
                }.onSuccess {
                    logger.info { "登陆成功, $it" }
                }
                delay(tasks.getValue(id).interval.last)
            }
        }
    }

    fun addTask(id: Long, name: String, subject: Contact): Unit = synchronized(tasks) {
        tasks.compute(id) { _, info ->
            (info ?: WeiboTaskInfo(name = name)).run {
                copy(contacts = contacts + subject.toContactInfo())
            }
        }
        taskJobs.compute(id) { _, job ->
            job?.takeIf { it.isActive } ?: addListener(id)
        }
    }

    fun removeTask(id: Long, subject: Contact): Unit = synchronized(tasks) {
        tasks.compute(id) { _, info ->
            info?.run {
                copy(contacts = contacts - subject.toContactInfo())
            }
        }
        if (taskContactInfos(id).isEmpty()) {
            taskJobs.remove(id)?.cancel()
        }
    }
}