package xyz.cssxsh.mirai.weibo.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.contact.*
import xyz.cssxsh.mirai.weibo.*
import xyz.cssxsh.mirai.weibo.data.*
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*

object WeiboUserCommand : CompositeCommand(
    owner = WeiboHelperPlugin,
    "wuser", "微博用户",
    description = "微博用户指令",
), WeiboHelperCommand {

    private val subscriber = object : WeiboSubscriber<Long>(primaryName) {
        override val load: suspend (Long) -> List<MicroBlog> = { id ->
            client.getUserMicroBlogs(uid = id, page = 1).list
        }

        override val reposts: Boolean = false

        override val tasks: MutableMap<Long, WeiboTaskInfo> by WeiboTaskData::users
    }

    @SubCommand("add", "task", "订阅")
    suspend fun CommandSender.task(uid: Long, subject: Contact = subject()) {
        val user = client.getUserInfo(uid = uid).user
        subscriber.add(id = user.id, name = user.screen, subject = subject)
        sendMessage("对@${user.screen}#${user.id}的监听任务, 添加完成")
    }

    @SubCommand("stop", "停止")
    suspend fun CommandSender.stop(uid: Long, subject: Contact = subject()) {
        subscriber.remove(id = uid, subject = subject)
        sendMessage("对User(${uid})的监听任务, 取消完成")
    }

    @SubCommand("detail", "详情")
    suspend fun CommandSender.detail(subject: Contact = subject()) {
        sendMessage(subscriber.detail(subject = subject))
    }
}