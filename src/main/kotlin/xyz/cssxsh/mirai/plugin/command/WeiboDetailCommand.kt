package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import xyz.cssxsh.mirai.plugin.*
import xyz.cssxsh.weibo.api.*

object WeiboDetailCommand : SimpleCommand(
    owner = WeiboHelperPlugin,
    "wdetail", "blog", "微博详情",
    description = "微博详情指令",
) {

    @Handler
    suspend fun CommandSenderOnMessage<*>.hendle(mid: String) = sendMessage { client.getMicroBlog(mid).toMessage(it) }
}