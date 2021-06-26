package xyz.cssxsh.weibo

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*
import java.nio.charset.Charset
import java.time.format.DateTimeFormatter

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

inline fun <reified T> TempData.data(): T = WeiboClient.Json.decodeFromJsonElement(requireNotNull(data) {
    if (url.orEmpty().startsWith(LOGIN_PAGE)) {
        "登陆状态无效，请登录"
    } else {
        toString()
    }
})

internal suspend inline fun <reified T> WeiboClient.temp(
    url: String,
    crossinline block: HttpRequestBuilder.() -> Unit
) = get<TempData>(url, block).data<T>()

internal suspend inline fun <reified T> WeiboClient.callback(
    url: String,
    crossinline block: HttpRequestBuilder.() -> Unit = {}
) = WeiboClient.Json.decodeFromString<T>(get<String>(url, block).substringAfter('(').substringBeforeLast(')'))

suspend inline fun <reified T> WeiboClient.get(
    url: String,
    crossinline block: HttpRequestBuilder.() -> Unit = {}
): T = useHttpClient { client -> client.get(url, block) }

suspend inline fun <reified T> WeiboClient.get(url: Url): T = useHttpClient { client -> client.get(url) }

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

internal fun image(pid: String) = Url("https://${ImageServer.random()}.sinaimg.cn/large/${pid}")

val MicroBlog.link get() = "https://weibo.com/detail/${id}"

val MicroBlog.username get() = user?.screen ?: "[未知用户]"

val MicroBlog.uid get() = user?.id ?: 0

val MicroBlog.datetime: String get() = created.format(DateTimeFormatter.ISO_LOCAL_DATE)

fun UserGroupData.getGroup(id: Long) = groups.flatMap { it.list }.first { it.gid == id }
