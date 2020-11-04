package xyz.cssxsh.mirai.plugin.command

import io.ktor.client.request.*
import kotlinx.coroutines.*
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.BotOnlineEvent
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.asMessageChain
import net.mamoe.mirai.message.uploadAsImage
import xyz.cssxsh.mirai.plugin.WeiboHelperPlugin
import xyz.cssxsh.mirai.plugin.data.WeiboTaskData
import xyz.cssxsh.weibo.WeiboClient
import xyz.cssxsh.weibo.api.cardData
import xyz.cssxsh.weibo.api.getBlogs
import kotlin.coroutines.CoroutineContext

object WeiboCommand : CompositeCommand(
    owner = WeiboHelperPlugin,
    "weibo", "微博",
    description = "缓存指令",
), CoroutineScope {

    @ExperimentalCommandDescriptors
    @ConsoleExperimentalApi
    override val prefixOptional: Boolean = true

    private val logger get() = WeiboHelperPlugin.logger

    override val coroutineContext: CoroutineContext = CoroutineName("Weibo-Listener")

    private val taskJobs = mutableMapOf<Long, Job>()

    private val taskContacts = mutableMapOf<Long, Set<Contact>>()

    private val weiboClient = WeiboClient(emptyMap())

    private fun WeiboTaskData.TaskInfo.getContacts(bot: Bot): Set<Contact> =
        (bot.groups.filter { it.id in groups } + bot.friends.filter { it.id in friends }).toSet()

    fun onInit() = WeiboHelperPlugin.subscribeAlways<BotOnlineEvent> {
        logger.info("开始初始化${bot}联系人列表")
        WeiboTaskData.tasks.toMap().forEach { (uid, info) ->
            taskContacts[uid] = info.getContacts(bot)
            addListener(uid)
        }
    }

    private suspend fun List<Any>.sendMessageToTaskContacts(uid: Long) = taskContacts.getValue(uid).forEach { contact ->
        contact.runCatching {
            sendMessage(map {
                when(it) {
                    is String -> PlainText(it)
                    is Message -> it
                    is ByteArray -> it.inputStream().uploadAsImage(contact)
                    else -> PlainText(it.toString())
                }
            }.asMessageChain())
        }
    }

    private fun addListener(uid: Long): Job = launch {
        val intervalMillis = WeiboTaskData.tasks.getValue(uid).run {
            minIntervalMillis..maxIntervalMillis
        }
        while (isActive && taskContacts[uid].isNullOrEmpty().not()) {
            runCatching {
                weiboClient.cardData(uid).getBlogs().apply {
                    sortedBy {
                        it.id.toLong()
                    }.filter {
                        it.id.toLong() > WeiboTaskData.tasks.getOrPut(uid) { WeiboTaskData.TaskInfo() }.last
                    }.forEach { blog ->
                        buildList<Any> {
                            add(buildString {
                                appendLine("微博${blog.user.screenName}有新动态：")
                                append(blog.rawText)
                            })
                            addAll(blog.pics.mapNotNull { pid ->
                                runCatching {
                                    weiboClient.useHttpClient<ByteArray> { it.get(pid.large.url) }
                                }.getOrNull()
                            })
                        }.sendMessageToTaskContacts(uid)
                    }
                    maxByOrNull { it.id.toLong() }?.let { blog ->
                        logger.verbose("(${uid})[${blog.user.screenName}]>最新微博号为<${blog.id}>")
                        WeiboTaskData.tasks.compute(uid) { _, info ->
                            info?.copy(last = blog.id.toLong())
                        }
                    }
                }
            }.onSuccess {
                delay(intervalMillis.random().also {
                    logger.info("(${uid}): ${WeiboTaskData.tasks[uid]}监听任务完成一次, 即将进入延时delay(${it}ms)。")
                })
            }.onFailure {
                logger.warning("(${uid})监听任务执行失败", it)
                delay(intervalMillis.last)
            }
        }
    }.also { logger.info("添加对${uid}的监听任务, 添加完成${it}") }

    private fun MutableMap<Long, Set<Contact>>.addUid(uid: Long, subject: Contact) = compute(uid) { _, list ->
        (list ?: emptySet()) + subject.also { contact ->
            WeiboTaskData.tasks.compute(uid) { _, info ->
                (info ?: WeiboTaskData.TaskInfo()).run {
                    when (contact) {
                        is Friend -> copy(friends = friends + contact.id)
                        is Group -> copy(groups = groups + contact.id)
                        else -> this
                    }
                }
            }
        }
    }

    private fun MutableMap<Long, Set<Contact>>.removeUid(uid: Long, subject: Contact) = compute(uid) { _, list ->
        (list ?: emptySet()) - subject.also {
            WeiboTaskData.tasks.remove(uid)
        }
    }

    @SubCommand("task", "订阅")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.task(uid: Long) = runCatching {
        taskContacts.addUid(uid, fromEvent.subject)
        taskJobs.compute(uid) { _, job ->
            job?.takeIf { it.isActive } ?: addListener(uid)
        }
    }.onSuccess { job ->
        quoteReply("对${uid}的监听任务, 添加完成${job}")
    }.onFailure {
        quoteReply(it.toString())
    }.isSuccess

    @SubCommand("stop", "停止")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.stop(uid: Long) = runCatching {
        taskContacts.removeUid(uid, fromEvent.subject)
        taskJobs.compute(uid) { _, job ->
            if (taskContacts[uid].isNullOrEmpty()) {
                WeiboTaskData.tasks.remove(uid)
                null
            } else {
                job
            }
        }
    }.onSuccess { job ->
        quoteReply("对${uid}的监听任务, 取消完成${job}")
    }.onFailure {
        quoteReply(it.toString())
    }.isSuccess

    @SubCommand("list", "列表")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.list() = runCatching {
        buildString {
            appendLine("监听状态:")
            WeiboTaskData.tasks.toMap().forEach { (uid, info) ->
                if (fromEvent.subject.id in info.groups + info.friends) {
                    appendLine("$uid -> ${taskJobs.getValue(uid)}")
                }
            }
        }
    }.onSuccess { text ->
        quoteReply(text)
    }.onFailure {
        quoteReply(it.toString())
    }.isSuccess
}