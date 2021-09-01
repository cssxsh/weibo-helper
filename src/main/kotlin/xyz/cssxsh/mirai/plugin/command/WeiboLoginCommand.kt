package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import xyz.cssxsh.mirai.plugin.*
import xyz.cssxsh.weibo.api.*

object WeiboLoginCommand : SimpleCommand(
    owner = WeiboHelperPlugin,
    "wlogin", "微博登录",
    description = "微博登录指令",
) {
    @Handler
    suspend fun CommandSenderOnMessage<*>.hendle() = sendMessage {
        runCatching {
            client.qrcode { image ->
                sendMessage(image.inputStream().uploadAsImage(it) + "请使用微博客户端扫码")
            }
        }.onFailure {
            logger.warning(it)
        }.mapCatching {
            "@${it.info.display}#${it.info.uid} 登陆成功".toPlainText()
        }.getOrThrow()
    }
}