package xyz.cssxsh.weibo

import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*
import java.nio.charset.*

fun Boolean.toInt() = if (this) 1 else 0

@Serializable
data class TempData(
    @SerialName("data")
    val `data`: JsonElement? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("http_code")
    val httpCode: Int = 200,
    @SerialName("ok")
    @Serializable(NumberToBooleanSerializer::class)
    val ok: Boolean = true
)

const val ErrorMessageLength = 32

suspend inline fun <reified T> WeiboClient.temp(url: String, crossinline block: HttpRequestBuilder.() -> Unit): T {
    val text = text(url, block)
    val temp = WeiboClient.Json.decodeFromString<TempData>(text)
    val data = requireNotNull(temp.data) {
        if (temp.url.orEmpty().startsWith(LOGIN_PAGE)) {
            "登陆状态无效，请登录"
        } else {
            text.substring(0, minOf(ErrorMessageLength, text.length))
        }
    }
    return WeiboClient.Json.decodeFromJsonElement(data)
}

suspend inline fun <reified T> WeiboClient.callback(url: String, crossinline block: HttpRequestBuilder.() -> Unit): T {
    val json = text(url, block).substringAfter('(').substringBefore(')')
    return runCatching {
        WeiboClient.Json.decodeFromString<T>(json)
    }.getOrElse {
        throw IllegalArgumentException(json, it)
    }
}

suspend inline fun WeiboClient.text(url: String, crossinline block: HttpRequestBuilder.() -> Unit): String {
    return useHttpClient { client -> client.get(url, block) }
}

suspend inline fun <reified T> WeiboClient.json(url: String, crossinline block: HttpRequestBuilder.() -> Unit): T {
    val text = text(url, block)
    val temp = WeiboClient.Json.decodeFromString<TempData>(text)
    require(temp.ok) {
        if (temp.url.orEmpty().startsWith(LOGIN_PAGE)) {
            "登陆状态无效，请登录"
        } else {
            text.substring(0, minOf(ErrorMessageLength, text.length))
        }
    }
    return WeiboClient.Json.decodeFromString(text)
}

suspend inline fun WeiboClient.download(url: String, min: Long = 1024): ByteArray = useHttpClient { client ->
    client.get<HttpResponse>(url) {
        header(HttpHeaders.Referrer, INDEX_PAGE)
    }.also { response ->
        val length = response.contentLength() ?: 0
        if (length < min) {
            throw ClientRequestException(response, response.readText())
        }
    }.receive()
}

/**
 * 国标码
 * Chinese Internal Code Specification
 */
@Suppress("unused")
internal val Charsets.GBK get() = Charset.forName("GBK")

internal const val EncodeChars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

internal fun String.decodeBase62() = fold(0L) { acc, char -> acc * 62 + EncodeChars.indexOf(char) }

internal const val WEIBO_EPOCH = 515483463L

internal fun timestamp(id: Long): Long = (id shr 22) + WEIBO_EPOCH

internal fun id(mid: String): Long {
    return mid.substring(0..0).decodeBase62().times(1_0000000_0000000L) +
        mid.substring(1..4).decodeBase62().times(1_0000000L) +
        mid.substring(5..8).decodeBase62()
}

val ImageServer = listOf("wx1.sinaimg.cn", "wx2.sinaimg.cn", "wx3.sinaimg.cn", "wx4.sinaimg.cn")

val ImageExtensions = mapOf(
    ContentType.Image.JPEG to "jpg",
    ContentType.Image.GIF to "gif",
    ContentType.Image.PNG to "png",
)

fun user(pid: String): Long = with(pid.substring(0..7)) {
    if (startsWith("00")) decodeBase62() else toLong(16)
}

fun extension(pid: String) = ImageExtensions.values.first { it.startsWith(pid[21]) }

fun image(pid: String, server: String = ImageServer.random()) = "https://${server}/large/${pid}.${extension(pid)}"

fun download(pid: String) = "https://weibo.com/ajax/common/download?pid=${pid}"

val MicroBlog.link get() = "https://weibo.com/${user?.id ?: "detail"}/${mid}"

val MicroBlog.username get() = user?.screen ?: "[未知用户]"

val MicroBlog.uid get() = user?.id ?: 0

fun UserGroupData.getGroup(id: Long) = groups.flatMap { it.list }.first { it.gid == id }
