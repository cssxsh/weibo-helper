package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.message.data.toPlainText
import net.mamoe.mirai.utils.verbose
import xyz.cssxsh.mirai.plugin.*
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*

object WeiboHotCommand : CompositeCommand(
    owner = WeiboHelperPlugin,
    "whot", "微博热搜",
    description = "微博分组指令",
) {
    private val keyword by WeiboTaskData::keyword

    internal val listener: WeiboListener = object : WeiboListener("Hot") {

        override val load: suspend (Long) -> List<MicroBlog> = { timestamp ->
            client.search(keyword = keyword.getValue(timestamp), type = ChannelType.HOT).cards.mapNotNull { it.blog }
        }

        override val predicate: (MicroBlog, Long, MutableSet<Long>) -> Boolean = filter@{ blog, id, histories ->
            val source = blog.retweeted ?: blog
            if (source.reposts < filter.repost) {
                logger.verbose { "${type}(${id}) 转发数屏蔽，跳过 ${source.id} ${source.reposts}" }
                return@filter false
            }
            super.predicate(blog, id, histories)
        }

        override val tasks: MutableMap<Long, WeiboTaskInfo> by WeiboTaskData::hot
    }

    @SubCommand("add", "task", "订阅")
    suspend fun CommandSenderOnMessage<*>.task(word: String) = sendMessage {
        val timestamp = System.currentTimeMillis()
        keyword[timestamp] = word
        listener.add(id = timestamp, name = word, subject = fromEvent.subject)
        "对${word}的监听任务, 添加完成".toPlainText()
    }

    @SubCommand("stop", "停止")
    suspend fun CommandSenderOnMessage<*>.stop(timestamp: Long) = sendMessage {
        listener.remove(id = timestamp, subject = fromEvent.subject)
        "对${keyword[timestamp]}的监听任务, 取消完成".toPlainText()
    }

    @SubCommand("detail", "详情")
    suspend fun CommandSenderOnMessage<*>.detail() = sendMessage {
        listener.detail(subject = fromEvent.subject).toPlainText()
    }
}