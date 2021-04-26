package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import xyz.cssxsh.mirai.plugin.*
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.mirai.plugin.WeiboHelperPlugin.client
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*
import xyz.cssxsh.weibo.data.feed.UserGroupType

object WeiboGroupCommand : CompositeCommand(
    owner = WeiboHelperPlugin,
    "wgroup", "微博分组",
    description = "微博分组指令",
) {
    internal val listener: WeiboListener = object : WeiboListener() {
        override val type: String = "Group"

        override val load: suspend (Long) -> List<SimpleMicroBlog> = {
            client.getTimeline(gid = it, type = TimelineType.GROUPS).statuses
        }

        override val tasks: MutableMap<Long, WeiboTaskInfo>
            get() = WeiboTaskData.groups
    }

    @SubCommand("list", "列表")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.list() = kotlin.runCatching {
        client.getFeedGroups().buildMessage { it.type != UserGroupType.SYSTEM }
    }.onSuccess { message ->
        sendMessage(fromEvent.message.quote() + message)
    }.onFailure {
        sendMessage(fromEvent.message.quote() + it.toString())
    }.isSuccess

    @SubCommand("task", "订阅")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.task(gid: Long) = runCatching {
        client.getFeedGroups().getGroup(id = gid).apply { listener.addTask(id = gid, name = title, subject = fromEvent.subject) }
    }.onSuccess { group ->
        sendMessage(fromEvent.message.quote() + "对分组${group.title}#${gid}的监听任务, 添加完成")
    }.onFailure {
        sendMessage(fromEvent.message.quote() + it.toString())
    }.isSuccess

    @SubCommand("stop", "停止")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.stop(gid: Long) = runCatching {
        listener.removeTask(id = gid, subject = fromEvent.subject)
    }.onSuccess {
        sendMessage(fromEvent.message.quote() + "对Group(${gid})的监听任务, 取消完成")
    }.onFailure {
        sendMessage(fromEvent.message.quote() + it.toString())
    }.isSuccess
}