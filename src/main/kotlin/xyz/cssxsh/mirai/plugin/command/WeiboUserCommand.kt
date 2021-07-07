package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.toPlainText
import xyz.cssxsh.mirai.plugin.*
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*

object WeiboUserCommand : CompositeCommand(
    owner = WeiboHelperPlugin,
    "wuser", "微博用户",
    description = "微博好友指令",
) {
    internal val listener: WeiboListener = object : WeiboListener("User") {

        override val load: suspend (id: Long) -> List<MicroBlog> = { id -> client.getUserMicroBlogs(id, 1).list }

        override val tasks: MutableMap<Long, WeiboTaskInfo> by WeiboTaskData::users
    }

    @SubCommand("add", "task", "订阅")
    suspend fun CommandSenderOnMessage<*>.task(uid: Long) = sendMessage {
        val user = client.getUserInfo(uid = uid).user
        listener.addTask(id = user.id, name = user.screen, subject = fromEvent.subject)
        "对@${user.screen}#${uid}的监听任务, 添加完成".toPlainText()
    }

    @SubCommand("stop", "停止")
    suspend fun CommandSenderOnMessage<*>.stop(uid: Long) = sendMessage {
        listener.removeTask(id = uid, subject = fromEvent.subject)
        "对User(${uid})的监听任务, 取消完成".toPlainText()
    }

    @SubCommand("detail", "详情")
    suspend fun CommandSenderOnMessage<*>.detail() = sendMessage {
        listener.detail(subject = fromEvent.subject).toPlainText()
    }
}