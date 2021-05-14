package xyz.cssxsh.mirai.plugin.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.message.data.toPlainText
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import xyz.cssxsh.mirai.plugin.*
import xyz.cssxsh.weibo.api.qrcode

object WeiboLoginCommand : SimpleCommand(
    owner = WeiboHelperPlugin,
    "wlogin", "微博登录",
    description = "微博登录指令",
) {
    @Handler
    suspend fun CommandSenderOnMessage<*>.hendle() = sendMessage {
        client.qrcode { image ->
            sendMessage("请使用微博客户端扫码：")
            sendMessage(image.toExternalResource().uploadAsImage(fromEvent.subject))
        }
        "@${client.info.display}#${client.info.uid} 登陆成功".toPlainText()
    }
}