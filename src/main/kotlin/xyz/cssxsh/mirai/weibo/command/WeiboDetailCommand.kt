package xyz.cssxsh.mirai.weibo.command

import net.mamoe.mirai.console.command.*
import xyz.cssxsh.mirai.weibo.*
import xyz.cssxsh.weibo.api.*

@PublishedApi
internal object WeiboDetailCommand : SimpleCommand(
    owner = WeiboHelperPlugin,
    "wdetail", "blog", "微博详情",
    description = "微博详情指令",
) {

    @Handler
    suspend fun CommandSenderOnMessage<*>.hendle(mid: String) = quote { client.getMicroBlog(mid).toMessage(it) }
}