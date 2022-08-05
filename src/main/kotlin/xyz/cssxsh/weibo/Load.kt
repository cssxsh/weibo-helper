package xyz.cssxsh.weibo

import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.*
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

suspend inline fun WeiboClient.text(url: String, crossinline block: HttpRequestBuilder.() -> Unit): String {
    return useHttpClient { client -> client.prepareGet(url, block).body() }
}

suspend inline fun <reified T> WeiboClient.temp(url: String, crossinline block: HttpRequestBuilder.() -> Unit): T {
    val text = text(url, block)
    check(text.startsWith("{")) { text.substring(0, minOf(ErrorMessageLength, text.length)) }
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
    return try {
        WeiboClient.Json.decodeFromString(json)
    } catch (e: Throwable) {
        throw IllegalArgumentException(json, e)
    }
}

suspend inline fun <reified T> WeiboClient.json(url: String, crossinline block: HttpRequestBuilder.() -> Unit): T {
    val text = text(url, block)
    check(text.startsWith("{")) { text.substring(0, minOf(ErrorMessageLength, text.length)) }
    val temp = WeiboClient.Json.decodeFromString<TempData>(text)
    check(temp.ok) {
        if (temp.url.orEmpty().startsWith(LOGIN_PAGE)) {
            "登陆状态无效，请登录"
        } else {
            text.substring(0, minOf(ErrorMessageLength, text.length))
        }
    }
    return WeiboClient.Json.decodeFromString(text)
}

suspend fun WeiboClient.download(url: String, min: Long = 1024): ByteArray = useHttpClient { client ->
    client.prepareGet(url) {
        header(HttpHeaders.Referrer, INDEX_PAGE)
    }.execute { response ->
        // 部分 response 没有 ContentLength, 直接返回，例如验证码
        val length = response.contentLength() ?: Long.MAX_VALUE
        if (length < min) {
            throw ClientRequestException(response, response.body())
        }
        response.body()
    }
}

suspend fun WeiboClient.download(pid: String, index: Int): ByteArray = useHttpClient { client ->
    client.prepareGet(image(pid = pid, server = ImageServer.random(), index = index)) {
        header(HttpHeaders.Referrer, INDEX_PAGE)
    }.body()
}

suspend fun WeiboClient.download(video: PageInfo.MediaInfo.PlayInfo) = flow<ByteArray> {
    for (offset in 0 until video.size step video.buffer) {
        val limit = (offset + video.buffer).coerceAtMost(video.size) - 1
        emit(useHttpClient { client ->
            client.prepareGet(video.url) {
                header(HttpHeaders.Range, "bytes=${offset}-${limit}")
            }.body()
        })
    }
}

/**
 * 国标码
 * Chinese Internal Code Specification
 */
@Suppress("unused")
internal val Charsets.GBK
    get() = Charset.forName("GBK")

internal const val EncodeChars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

internal fun String.decodeBase62() = fold(0L) { acc, char ->
    val index = EncodeChars.indexOf(char)
    check(index != -1) { "$char no 62" }
    acc * 62 + index
}

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

fun image(pid: String, server: String, index: Int) = "https://${server}/large/${pid}.${extension(pid)}#${index}"

fun picture(pid: String, index: Int) = "https://weibo.com/ajax/common/download?pid=${pid}#${index}"

val MicroBlog.link get() = "https://weibo.com/${user?.id ?: "detail"}/${mid}"

val MicroBlog.username get() = user?.screen ?: "[未知用户]"

val MicroBlog.uid get() = user?.id ?: 0

operator fun UserGroupData.get(id: String): UserGroup {
    for (category in groups) {
        for (group in category.list) {
            if (group.gid == id.toLongOrNull()) return group
            if (group.title == id) return group
        }
    }
    throw NoSuchElementException("Group: $id")
}
