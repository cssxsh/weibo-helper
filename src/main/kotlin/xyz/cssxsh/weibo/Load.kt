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
import java.time.format.*

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

suspend inline fun WeiboClient.download(url: String): ByteArray = useHttpClient { client ->
    client.get<HttpResponse>(url) {
        header(HttpHeaders.Referrer, INDEX_PAGE)
    }.also { response ->
        val length = response.contentLength() ?: return@also
        if (length < 1024) {
            throw ClientRequestException(response, response.readText())
        }
    }.receive()
}

internal val ChineseCharset = Charset.forName("GBK")

internal val EncodeChars = ('0'..'9').asIterable() + ('a'..'z').asIterable() + ('A'..'Z').asIterable()

internal fun String.decodeBase62() = fold(0L) { acc, char -> acc * 62 + EncodeChars.indexOf(char) }

internal fun user(pid: String): Long = pid.substring(0..7).run {
    if (startsWith("00")) decodeBase62() else toLong(16)
}

internal const val WEIBO_EPOCH = 515483463L

internal fun timestamp(id: Long): Long = (id shr 22) + WEIBO_EPOCH

private val ImageServer = listOf("wx1", "wx2", "wx3", "wx4")

internal val ImageExtensions = mapOf(
    ContentType.Image.JPEG to "jpg",
    ContentType.Image.GIF to "gif",
    ContentType.Image.PNG to "png",
)

internal fun extension(pid: String) = ImageExtensions.values.first { it.startsWith(pid[21]) }

internal fun image(pid: String) = "https://${ImageServer.random()}.sinaimg.cn/large/${pid}.${extension(pid)}"

internal fun download(pid: String) = "https://weibo.com/ajax/common/download?pid=${pid}"

val MicroBlog.link get() = "https://weibo.com/${user?.id ?: "detail"}/${mid}"

val MicroBlog.username get() = user?.screen ?: "[未知用户]"

val MicroBlog.uid get() = user?.id ?: 0

val MicroBlog.datetime: String get() = created.format(DateTimeFormatter.ISO_LOCAL_DATE)

fun UserGroupData.getGroup(id: Long) = groups.flatMap { it.list }.first { it.gid == id }
