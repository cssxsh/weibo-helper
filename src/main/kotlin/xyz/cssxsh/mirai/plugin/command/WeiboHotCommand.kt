package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.mirai.plugin.*
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*

object WeiboHotCommand : CompositeCommand(
    owner = WeiboHelperPlugin,
    "whot", "微博热搜",
    description = "微博分组指令",
) {
    internal val subscriber = object : WeiboSubscriber<String>("Hot") {

        override val load: suspend (String) -> List<MicroBlog> = { keyword ->
            client.search(keyword = keyword, type = ChannelType.HOT).cards.mapNotNull { it.blog }
        }

        override val tasks: MutableMap<String, WeiboTaskInfo> by WeiboTaskData::hots
    }

    @SubCommand("add", "task", "订阅")
    suspend fun CommandSenderOnMessage<*>.task(word: String) = sendMessage {
        subscriber.add(id = word, name = word, subject = fromEvent.subject)
        "对<${word}>的监听任务, 添加完成".toPlainText()
    }

    @SubCommand("stop", "停止")
    suspend fun CommandSenderOnMessage<*>.stop(word: String) = sendMessage {
        subscriber.remove(id = word, subject = fromEvent.subject)
        "对<${word}>的监听任务, 取消完成".toPlainText()
    }

    @SubCommand("detail", "详情")
    suspend fun CommandSenderOnMessage<*>.detail() = sendMessage {
        subscriber.detail(subject = fromEvent.subject).toPlainText()
    }
}