package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.toPlainText
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

        override val load: suspend (id: Long) -> List<SimpleMicroBlog> = { id ->
            client.getTimeline(gid = id, type = TimelineType.GROUPS).statuses
        }

        override val tasks: MutableMap<Long, WeiboTaskInfo>
            get() = WeiboTaskData.groups
    }

    @SubCommand("list", "列表")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<*>.list() = sendMessage {
        client.getFeedGroups().buildMessage { it.type != UserGroupType.SYSTEM }
    }

    @SubCommand("task", "订阅")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<*>.task(gid: Long) = sendMessage {
        val group = client.getFeedGroups().getGroup(id = gid)
        listener.addTask(id = gid, name = group.title, subject = fromEvent.subject)
        "对分组${group.title}#${gid}的监听任务, 添加完成".toPlainText()
    }

    @SubCommand("stop", "停止")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<*>.stop(gid: Long) = sendMessage {
        listener.removeTask(id = gid, subject = fromEvent.subject)
        "对Group(${gid})的监听任务, 取消完成".toPlainText()
    }
}