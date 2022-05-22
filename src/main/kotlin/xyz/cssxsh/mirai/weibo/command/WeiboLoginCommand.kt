package xyz.cssxsh.mirai.weibo.command

import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import xyz.cssxsh.mirai.weibo.*
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.*

object WeiboLoginCommand : SimpleCommand(
    owner = WeiboHelperPlugin,
    "wlogin", "微博登录",
    description = "微博登录指令",
), WeiboHelperCommand {

    @Handler
    suspend fun CommandSenderOnMessage<*>.hendle() = sendMessage { contact ->
        client.runCatching {
            qrcode { url ->
                logger.info("qrcode: $url")
                launch {
                    val image = try {
                        withTimeout(60_000) {
                            client.download(url).toExternalResource().use { it.uploadAsImage(contact) }
                        }
                    } catch (_: Throwable) {
                        url.toPlainText()
                    }

                    sendMessage(image)
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
                    sendMessage(url)
                }
            }
        }.onFailure {
            logger.warning(it)
        }.mapCatching {
            sendMessage("@${it.info.display}#${it.info.uid} 登陆成功")
        }.getOrThrow()
    }
}