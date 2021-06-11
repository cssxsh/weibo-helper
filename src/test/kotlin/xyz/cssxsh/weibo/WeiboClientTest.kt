package xyz.cssxsh.weibo

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import net.mamoe.yamlkt.Yaml
import xyz.cssxsh.weibo.data.*
import java.io.File

internal abstract class WeiboClientTest {

    val list = listOf(
        5174017612L,
        6787924129L
    )

    val client by lazy { WeiboClient(status) }

    val test = File("./test/")

    val qrcode = test.resolve("qrcode.jpg")

    val yaml = test.resolve("status.yaml")

    fun MicroBlog.toText() = buildString {
        appendLine("微博 $username 有新动态：")
        appendLine("时间: $created")
        appendLine("链接: $link")
        appendLine(raw)
        pictures.forEach {
            appendLine(it)
        }
        retweeted?.let { retweeted ->
            appendLine("==============================")
            appendLine("@${retweeted.username}")
            appendLine("时间: ${retweeted.created}")
            appendLine("链接: ${retweeted.link}")
            appendLine(retweeted.raw)
            retweeted.pictures.forEach {
                appendLine(it)
            }
        }
    }

    var status: LoginStatus
        get() = runCatching<LoginStatus> { Yaml.decodeFromString(yaml.readText()) }.getOrElse { LoginStatus() }
        set(value) {
            yaml.writeText(Yaml.encodeToString<LoginStatus>(value))
        }
}