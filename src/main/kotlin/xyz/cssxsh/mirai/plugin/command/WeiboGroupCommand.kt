package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.toPlainText
import net.mamoe.mirai.utils.*
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
    internal val listener: WeiboListener = object : WeiboListener("Group") {

        override val load: suspend (Long) -> List<MicroBlog> = { id ->
            client.getGroupsTimeline(gid = id, count = 100).statuses
        }

        override val predicate: (MicroBlog, Long) -> Boolean = filter@{ blog, id ->
            val source = blog.retweeted ?: blog
            if (source.reposts < filter.repost) {
                logger.verbose { "${type}(${id}) 转发数屏蔽，跳过 ${source.id} ${source.reposts}" }
                return@filter false
            }
            super.predicate(blog, id)
        }

        override val tasks: MutableMap<Long, WeiboTaskInfo> by WeiboTaskData::groups
    }

    @SubCommand("list", "列表")
    suspend fun CommandSenderOnMessage<*>.list() = sendMessage { client.getFeedGroups().toMessage() }

    @SubCommand("add", "task", "订阅")
    suspend fun CommandSenderOnMessage<*>.task(gid: Long) = sendMessage {
        val group = client.getFeedGroups().getGroup(id = gid)
        listener.add(id = gid, name = group.title, subject = fromEvent.subject)
        "对${group.title}#${gid}的监听任务, 添加完成".toPlainText()
    }

    @SubCommand("stop", "停止")
    suspend fun CommandSenderOnMessage<*>.stop(gid: Long) = sendMessage {
        listener.remove(id = gid, subject = fromEvent.subject)
        "对Group(${gid})的监听任务, 取消完成".toPlainText()
    }

    @SubCommand("detail", "详情")
    suspend fun CommandSenderOnMessage<*>.detail() = sendMessage {
        listener.detail(subject = fromEvent.subject).toPlainText()
    }
}