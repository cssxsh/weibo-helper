package xyz.cssxsh.weibo

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import xyz.cssxsh.weibo.data.*
import java.time.format.DateTimeFormatter

@Serializable
data class TempData(
    @SerialName("data")
    val `data`: JsonObject? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("http_code")
    val httpCode: Int = 200,
    @SerialName("ok")
    @Serializable(NumberToBooleanSerializer::class)
    val ok: Boolean = true
)

inline fun <reified T> TempData.data(): T = WeiboClient.json.decodeFromJsonElement(requireNotNull(data) { toString() })

internal suspend inline fun <reified T> WeiboClient.temp(
    url: String,
    crossinline block: HttpRequestBuilder.() -> Unit
) = get<TempData>(url, block).data<T>()

internal suspend inline fun <reified T> WeiboClient.get(
    url: String,
    crossinline block: HttpRequestBuilder.() -> Unit
): T = useHttpClient { client -> client.get(url, block) }

internal val Url.filename get() = encodedPath.substringAfterLast("/")

private const val CHAR_MIN = '0'.toLong()

internal fun String.toLong62() = fold(0L) { acc, c -> acc * 62 + (c.toLong() - CHAR_MIN) }

internal fun Url.getUser(): Long = filename.substring(0..7).let {
    if (it.startsWith("00")) it.toLong62() else it.toLong(16)
}

internal const val WEIBO_EPOCH = 515483463L

internal fun timestamp(id: Long): Long = (id shr 22) + WEIBO_EPOCH

val MicroBlog.images get() = pictureInfos.map { (_, picture) -> Url(picture.original.url) }

val MicroBlog.url get() = "https://weibo.com/detail/${id}"

val MicroBlog.username get() = user?.screen ?: "[未知用户]"

val MicroBlog.date: String get() = createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE)

fun UserGroupData.getGroup(id: Long) = groups.flatMap { it.list }.first { it.gid == id }
