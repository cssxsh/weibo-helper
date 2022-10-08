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
) {

    @Handler
    suspend fun CommandSenderOnMessage<*>.hendle() = quote { contact ->
        val result = client.qrcode { url ->
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
        "@${result.info.display}#${result.info.uid} 登陆成功".toPlainText()
    }

    @Handler
    suspend fun ConsoleCommandSender.hendle() {
        val message = try {
            val result = client.qrcode { url ->
                launch {
                    sendMessage(url)
                }
            }
            "@${result.info.display}#${result.info.uid} 登陆成功"
        } catch (cause: Exception) {
            cause.message ?: cause.toString()
        }
        sendMessage(message)
    }
}