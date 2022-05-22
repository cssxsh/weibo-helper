package xyz.cssxsh.mirai.weibo.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.contact.*
import xyz.cssxsh.mirai.weibo.*
import xyz.cssxsh.mirai.weibo.data.*
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*

object WeiboHotCommand : CompositeCommand(
    owner = WeiboHelperPlugin,
    "whot", "微博热搜",
    description = "微博热搜指令",
), WeiboHelperCommand {

    private val subscriber = object : WeiboSubscriber<String>(primaryName) {
        override val load: suspend (String) -> List<MicroBlog> = { keyword ->
            client.search(keyword = keyword, type = ChannelType.HOT).cards.mapNotNull { it.blog }
        }

        override val tasks: MutableMap<String, WeiboTaskInfo> by WeiboTaskData::hots
    }

    @SubCommand("add", "task", "订阅")
    suspend fun CommandSender.task(word: String, subject: Contact = subject()) {
        subscriber.add(id = word, name = word, subject = subject)
        sendMessage("对<${word}>的监听任务, 添加完成")
    }

    @SubCommand("stop", "停止")
    suspend fun CommandSender.stop(word: String, subject: Contact = subject()) {
        subscriber.remove(id = word, subject = subject)
        sendMessage("对<${word}>的监听任务, 取消完成")
    }

    @SubCommand("detail", "详情")
    suspend fun CommandSender.detail(subject: Contact = subject()) {
        sendMessage(subscriber.detail(subject = subject))
    }
}