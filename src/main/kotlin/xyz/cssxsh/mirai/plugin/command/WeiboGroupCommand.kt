package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.toPlainText
import xyz.cssxsh.mirai.plugin.*
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*
import xyz.cssxsh.weibo.getGroup

object WeiboGroupCommand : CompositeCommand(
    owner = WeiboHelperPlugin,
    "wgroup", "微博分组",
    description = "微博分组指令",
) {
    internal val listener: WeiboListener = object : WeiboListener() {
        override val type: String = "Group"

        override val load: suspend (id: Long) -> List<MicroBlog> = { id -> client.getTimeline(gid = id).statuses }

        override val tasks: MutableMap<Long, WeiboTaskInfo> by WeiboTaskData::groups
    }

    @SubCommand("list", "列表")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<*>.list() = sendMessage { client.getFeedGroups().buildMessage() }

    @SubCommand("add", "task", "订阅")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<*>.task(gid: Long) = sendMessage {
        val group = client.getFeedGroups().getGroup(id = gid)
        listener.addTask(id = gid, name = group.title, subject = fromEvent.subject)
        "对${group.title}#${gid}的监听任务, 添加完成".toPlainText()
    }

    @SubCommand("stop", "停止")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<*>.stop(gid: Long) = sendMessage {
        listener.removeTask(id = gid, subject = fromEvent.subject)
        "对Group(${gid})的监听任务, 取消完成".toPlainText()
    }
}