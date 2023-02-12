package xyz.cssxsh.mirai.weibo.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.contact.*
import xyz.cssxsh.mirai.weibo.*
import xyz.cssxsh.mirai.weibo.data.*
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*

@PublishedApi
internal object WeiboSuperChatCommand : CompositeCommand(
    owner = WeiboHelperPlugin,
    "wsc", "微博超话",
    description = "微博超话指令",
) {

    private val subscriber = object : WeiboSubscriber<String>(primaryName) {
        override val load: suspend (String) -> List<MicroBlog> = { id ->
            val data = client.getSuperChatData(id = id)
            buildList {
                for (card in data.cards) {
                    for (group in card.group) {
                        add(group.blog?.toMicroBlog() ?: continue)
                    }
                }
            }
        }

        override val tasks: MutableMap<String, WeiboTaskInfo> by WeiboTaskData::scs
    }

    @SubCommand("add", "task", "订阅")
    suspend fun CommandSender.task(id: String, subject: Contact = subject()) {
        val title = client.getSuperChatData(id = id).info.title
        subscriber.add(id = id, name = title, subject = subject)
        sendMessage("对<${title}>的监听任务, 添加完成")
    }

    @SubCommand("stop", "停止")
    suspend fun CommandSender.stop(id: String, subject: Contact = subject()) {
        subscriber.remove(id = id, subject = subject)
        sendMessage("对<${id}>的监听任务, 取消完成")
    }

    @SubCommand("detail", "详情")
    suspend fun CommandSender.detail(subject: Contact = subject()) {
        sendMessage(subscriber.detail(subject = subject))
    }
}