package xyz.cssxsh.mirai.weibo.command

import net.mamoe.mirai.console.command.*
import xyz.cssxsh.mirai.weibo.*
import xyz.cssxsh.weibo.api.*

@PublishedApi
internal object WeiboFollowCommand : SimpleCommand(
    owner = WeiboHelperPlugin,
    "wfollow", "微博关注",
    description = "微博关注指令",
) {

    @Handler
    suspend fun CommandSenderOnMessage<*>.hendle(uid: Long) = quote { client.follow(uid).toMessage(subject()) }
}