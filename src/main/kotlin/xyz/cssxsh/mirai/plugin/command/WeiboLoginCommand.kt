package xyz.cssxsh.mirai.plugin.command

import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import xyz.cssxsh.mirai.plugin.*
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.*

object WeiboLoginCommand : SimpleCommand(
    owner = WeiboHelperPlugin,
    "wlogin", "微博登录",
    description = "微博登录指令",
) {
    @Handler
    suspend fun CommandSenderOnMessage<*>.hendle() = sendMessage { contact ->
        client.runCatching {
            qrcode { url ->
                logger.info("qrcode: $url")
                launch {
                    val image = try {
                        client.download(url).toExternalResource().use { it.uploadAsImage(contact) }
                    } catch (e: Throwable) {
                        "$url ".toPlainText()
                    }

                    sendMessage(image + "请使用微博客户端扫码")
                }
            }
        }.onFailure {
            logger.warning(it)
        }.mapCatching {
            "@${it.info.display}#${it.info.uid} 登陆成功".toPlainText()
        }.getOrThrow()
    }

    @Handler
    suspend fun ConsoleCommandSender.hendle() {
        client.runCatching {
            qrcode { url ->
                launch {
                    sendMessage("$url 请使用微博客户端扫码")
                }
            }
        }.onFailure {
            logger.warning(it)
        }.mapCatching {
            "@${it.info.display}#${it.info.uid} 登陆成功".toPlainText()
        }.getOrThrow()
    }
}