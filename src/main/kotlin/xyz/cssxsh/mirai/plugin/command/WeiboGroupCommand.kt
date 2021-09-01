package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.contact.*
import xyz.cssxsh.mirai.plugin.*
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*
import xyz.cssxsh.weibo.*

object WeiboGroupCommand : CompositeCommand(
    owner = WeiboHelperPlugin,
    "wgroup", "微博分组",
    description = "微博分组指令",
) {
    internal val subscriber = object : WeiboSubscriber<Long>(primaryName) {
        override val load: suspend (Long) -> List<MicroBlog> = { id ->
            client.getGroupsTimeline(gid = id, count = 100).statuses
        }

        override val tasks: MutableMap<Long, WeiboTaskInfo> by WeiboTaskData::groups
    }

    @SubCommand("list", "列表")
    suspend fun CommandSender.list() = sendMessage(client.getFeedGroups().toMessage())

    @SubCommand("add", "task", "订阅")
    suspend fun CommandSender.task(gid: Long, subject: Contact = subject()) {
        val group = client.getFeedGroups().getGroup(id = gid)
        subscriber.add(id = gid, name = group.title, subject = subject)
        sendMessage("对${group.title}#${gid}的监听任务, 添加完成")
    }

    @SubCommand("stop", "停止")
    suspend fun CommandSender.stop(gid: Long, subject: Contact = subject()) {
        subscriber.remove(id = gid, subject = subject)
        sendMessage("对Group(${gid})的监听任务, 取消完成")
    }

    @SubCommand("detail", "详情")
    suspend fun CommandSender.detail(subject: Contact = subject()) = sendMessage(
        subscriber.detail(subject = subject)
    )
}