package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import xyz.cssxsh.mirai.plugin.*
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.weibo.WeiboClient
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*

object WeiboGroupCommand : CompositeCommand(
    owner = WeiboHelperPlugin,
    "wgroup", "微博分组",
    description = "微博分组指令",
) {
    internal val listener: WeiboListener = object : WeiboListener() {
        override val name: String = "Group"

        override suspend fun WeiboClient.loadMicroBlogs(id: Long): List<SimpleMicroBlog> =
            getTimeline(listId = id).statuses

        override val tasks: MutableMap<Long, WeiboTaskInfo>
            get() = WeiboTaskData.groups
    }

    @SubCommand("list", "列表")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.group() = kotlin.runCatching {
        WeiboHelperPlugin.weiboClient.getFeedGroups().buildMessage { it.type != 8888 }
    }.onSuccess { message ->
        sendMessage(fromEvent.message.quote() + message)
    }.onFailure {
        sendMessage(fromEvent.message.quote() + it.toString())
    }.isSuccess

    @SubCommand("task", "订阅")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.task(uid: Long) = runCatching {
        listener.addTask(uid, fromEvent.subject)
    }.onSuccess { job ->
        sendMessage(fromEvent.message.quote() + "对${uid}的监听任务, 添加完成${job}")
    }.onFailure {
        sendMessage(fromEvent.message.quote() + it.toString())
    }.isSuccess

    @SubCommand("stop", "停止")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.stop(uid: Long) = runCatching {
        listener.removeTask(uid, fromEvent.subject)
    }.onSuccess { job ->
        sendMessage(fromEvent.message.quote() + "对${uid}的监听任务, 取消完成${job}")
    }.onFailure {
        sendMessage(fromEvent.message.quote() + it.toString())
    }.isSuccess
}