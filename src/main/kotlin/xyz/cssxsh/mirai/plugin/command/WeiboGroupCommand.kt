package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.toPlainText
import xyz.cssxsh.mirai.plugin.*
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*
import xyz.cssxsh.weibo.getGroup
import xyz.cssxsh.weibo.uid

object WeiboGroupCommand : CompositeCommand(
    owner = WeiboHelperPlugin,
    "wgroup", "微博分组",
    description = "微博分组指令",
) {
    internal val listener: WeiboListener = object : WeiboListener("Group") {

        private val history = mutableSetOf<Long>()

        override val load: suspend (id: Long) -> List<MicroBlog> = { id ->
            client.getGroupsTimeline(gid = id, count = 100).statuses.filter { blog ->
                val source = blog.retweeted ?: blog
                if (source.uid in filter.users) return@filter false
                if (source.reposts < filter.repost) return@filter false
                if (filter.regexes.any { it in source.text }) return@filter false
                history.add(source.id)
            }
        }

        override val tasks: MutableMap<Long, WeiboTaskInfo> by WeiboTaskData::groups
    }

    @SubCommand("list", "列表")
    suspend fun CommandSenderOnMessage<*>.list() = sendMessage { client.getFeedGroups().toMessage() }

    @SubCommand("add", "task", "订阅")
    suspend fun CommandSenderOnMessage<*>.task(gid: Long) = sendMessage {
        val group = client.getFeedGroups().getGroup(id = gid)
        listener.addTask(id = gid, name = group.title, subject = fromEvent.subject)
        "对${group.title}#${gid}的监听任务, 添加完成".toPlainText()
    }

    @SubCommand("stop", "停止")
    suspend fun CommandSenderOnMessage<*>.stop(gid: Long) = sendMessage {
        listener.removeTask(id = gid, subject = fromEvent.subject)
        "对Group(${gid})的监听任务, 取消完成".toPlainText()
    }

    @SubCommand("detail", "详情")
    suspend fun CommandSenderOnMessage<*>.detail() = sendMessage {
        listener.detail(subject = fromEvent.subject).toPlainText()
    }
}