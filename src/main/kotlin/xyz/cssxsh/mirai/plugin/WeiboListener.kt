package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.weibo.WeiboClient
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*
import kotlin.coroutines.CoroutineContext

abstract class WeiboListener: CoroutineScope {

    abstract val name: String

    protected abstract suspend fun WeiboClient.loadMicroBlogs(id: Long): List<SimpleMicroBlog>

    override val coroutineContext: CoroutineContext by lazy {
        CoroutineName("WeiboListener-$name")
    }

    protected abstract val tasks : MutableMap<Long, WeiboTaskInfo>

    private val taskJobs = mutableMapOf<Long, Job>()

    private fun taskContactInfos(uid: Long) = tasks[uid]?.contacts.orEmpty()

    private fun taskContacts(uid: Long) = taskContactInfos(uid).mapNotNull { info ->
        Bot.findInstance(info.bot)?.takeIf { it.isOnline }?.run {
            when (info.type) {
                WeiboTaskInfo.ContactType.GROUP -> getGroup(info.id)
                WeiboTaskInfo.ContactType.FRIEND -> getFriend(info.id)
            }
        }
    }

    private val logger get() = WeiboHelperPlugin.logger

    private val client get() = WeiboHelperPlugin.weiboClient

    fun start() {
        tasks.forEach { (uid, _) ->
            addListener(uid)
        }
    }

    fun stop() {
        taskJobs.forEach { (_, job) ->
            job.cancel()
        }
        taskJobs.clear()
    }

    private suspend fun sendMessageToTaskContacts(
        id: Long,
        block: suspend MessageChainBuilder.(contact: Contact) -> Unit
    ) = taskContacts(id).forEach { contact ->
        runCatching {
            contact.sendMessage(buildMessageChain {
                block(contact)
            })
        }
    }

    private fun addListener(id: Long): Job = launch {
        delay(tasks.getValue(id).getInterval().random())
        while (isActive && taskContactInfos(id).isNotEmpty()) {
            runCatching {
                client.loadMicroBlogs(id).apply {
                    filter {
                        it.id > tasks.getValue(id).last
                    }.sortedBy { it.id }.forEach { blog ->
                        sendMessageToTaskContacts(id) { contact ->
                            blog.buildMessage(contact)
                        }
                    }
                    maxByOrNull { it.id }?.let { blog ->
                        logger.verbose { "(${id})[${blog.username}]最新微博号为<${blog.id}>" }
                        tasks.compute(id) { _, info ->
                            info?.copy(last = blog.id)
                        }
                    }
                }
            }.onSuccess {
                delay(tasks.getValue(id).getInterval().random().also {
                    WeiboHelperPlugin.logger.info { "$name(${id}): ${tasks[id]}监听任务完成一次, 即将进入延时delay(${it}ms)。" }
                })
            }.onFailure {
                WeiboHelperPlugin.logger.warning({ "$name(${id})监听任务执行失败，尝试重新登录" }, it)
                runCatching {
                    WeiboHelperPlugin.weiboClient.login()
                }.onSuccess {
                    WeiboHelperPlugin.logger.info { "登陆成功, $it" }
                }
                delay(tasks.getValue(id).maxIntervalMillis)
            }
        }
    }.also { WeiboHelperPlugin.logger.info { "添加对(${id})的监听任务, 添加完成${it}" } }

    fun addTask(id: Long, subject: Contact) = synchronized(tasks) {
        tasks.compute(id) { _, info ->
            (info ?: WeiboTaskInfo()).run {
                WeiboTaskInfo.ContactInfo(
                    id = subject.id,
                    bot = subject.bot.id,
                    type = when (subject) {
                        is Group -> WeiboTaskInfo.ContactType.GROUP
                        is Friend -> WeiboTaskInfo.ContactType.FRIEND
                        else -> throw IllegalArgumentException("不支持的联系人, $subject")
                    }
                ).let { info ->
                    copy(contacts = contacts + info)
                }
            }
        }
        taskJobs.compute(id) { _, job ->
            job?.takeIf { it.isActive } ?: addListener(id)
        }
    }

    fun removeTask(id: Long, subject: Contact) = synchronized(tasks) {
        tasks.compute(id) { _, info ->
            (info ?: WeiboTaskInfo()).run {
                copy(contacts = contacts.filter { it.id != subject.id })
            }
        }
        taskJobs[id]?.takeIf {
            taskContactInfos(id).isEmpty()
        }?.cancel()
    }
}