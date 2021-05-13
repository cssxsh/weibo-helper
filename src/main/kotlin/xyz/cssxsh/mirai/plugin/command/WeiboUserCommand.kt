package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.toPlainText
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

        override val load: suspend (id: Long) -> List<SimpleMicroBlog> = { id ->
            client.getUserMicroBlogs(uid = id).getMicroBlogs()
        }

        override val tasks: MutableMap<Long, WeiboTaskInfo> get() = WeiboTaskData.users
    }

    @SubCommand("task", "订阅")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<*>.task(uid: Long) = sendMessage {
        val user = client.getUserInfo(uid = uid).getUser()
        listener.addTask(id = user.id, name = user.name, subject = fromEvent.subject)
        "对@${user.name}#${uid}的监听任务, 添加完成".toPlainText()
    }

    @SubCommand("stop", "停止")
    @Suppress("unused")
    suspend fun CommandSenderOnMessage<*>.stop(uid: Long) = sendMessage {
        listener.removeTask(id = uid, subject = fromEvent.subject)
        "对User(${uid})的监听任务, 取消完成".toPlainText()
    }
}