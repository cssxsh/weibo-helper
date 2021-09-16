package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import xyz.cssxsh.mirai.plugin.*
import xyz.cssxsh.weibo.api.*

object WeiboFollowCommand : SimpleCommand(
    owner = WeiboHelperPlugin,
    "wfollow", "微博关注",
    description = "微博关注指令",
) {
    @Handler
    suspend fun CommandSenderOnMessage<*>.hendle(uid: Long) = sendMessage { client.follow(uid).toMessage(subject()) }
}