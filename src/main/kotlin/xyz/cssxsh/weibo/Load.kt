package xyz.cssxsh.weibo

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.mamoe.mirai.utils.warning
import xyz.cssxsh.mirai.plugin.WeiboHelperPlugin
import xyz.cssxsh.weibo.WeiboClient
import xyz.cssxsh.weibo.api.getLongText
import xyz.cssxsh.weibo.data.MicroBlog
import xyz.cssxsh.weibo.data.NumberToBooleanSerializer
import xyz.cssxsh.weibo.data.UserGroupData
import java.io.File
import java.time.format.DateTimeFormatter

@Serializable
private data class TempData<T>(
    @SerialName("data")
    val `data`: T? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("http_code")
    val httpCode: Int = 200,
    @SerialName("ok")
    @Serializable(NumberToBooleanSerializer::class)
    val ok: Boolean = true
)

private fun <T> TempData<T>.data() = requireNotNull(data) { toString() }

internal suspend fun <T> WeiboClient.temp(url: String,
    block: HttpRequestBuilder.() -> Unit
) = get<TempData<T>>(url, block).data()

internal suspend inline fun <reified T> WeiboClient.get(
    url: String,
    crossinline block: HttpRequestBuilder.() -> Unit
): T = useHttpClient { client -> client.get(url, block) }

suspend fun MicroBlog.getContent(): String {
    return if (continueTag != null) {
        runCatching {
            requireNotNull(WeiboHelperPlugin.client.getLongText(id).content) { "mid: $id" }
        }.getOrElse {
            WeiboHelperPlugin.logger.warning({ "获取微博[${id}]长文本失败" }, it)
            textRaw ?: text
        }
    } else {
        textRaw ?: text
    }
}

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

fun readHttpCookie(file: File): List<HttpCookie> = Json.decodeFromString(file.readText())
