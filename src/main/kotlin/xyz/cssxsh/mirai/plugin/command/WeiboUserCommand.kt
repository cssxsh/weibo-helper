package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import xyz.cssxsh.mirai.plugin.*
import xyz.cssxsh.mirai.plugin.WeiboHelperPlugin.client
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*

object WeiboUserCommand : CompositeCommand(
    owner = WeiboHelperPlugin,
    "wuser", "微博用户",
    description = "微博好友指令",
) {
    internal val listener: WeiboListener = object : WeiboListener() {
        override val type: String = "User"

        override val load: suspend (Long) -> List<SimpleMicroBlog> = {
            client.getUserMicroBlogs(uid = it).getMicroBlogs()
        }

        override val tasks: MutableMap<Long, WeiboTaskInfo>
            get() = WeiboTaskData.users
    }

    @SubCommand("task", "订阅")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.task(uid: Long) = runCatching {
        client.getUserInfo(uid = uid).getUser().apply { listener.addTask(id = id, name= name, subject = fromEvent.subject) }
    }.onSuccess { user ->
        sendMessage(fromEvent.message.quote() + "对@${user.name}#${uid}的监听任务, 添加完成")
    }.onFailure {
        sendMessage(fromEvent.message.quote() + it.toString())
    }.isSuccess

    @SubCommand("stop", "停止")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<MessageEvent>.stop(uid: Long) = runCatching {
        listener.removeTask(id = uid, subject = fromEvent.subject)
    }.onSuccess {
        sendMessage(fromEvent.message.quote() + "对User(${uid})的监听任务, 取消完成")
    }.onFailure {
        sendMessage(fromEvent.message.quote() + it.toString())
    }.isSuccess
}