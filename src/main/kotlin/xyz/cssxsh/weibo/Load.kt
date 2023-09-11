package xyz.cssxsh.weibo

import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*
import java.nio.charset.*

@PublishedApi
internal fun Boolean.toInt(): Int = if (this) 1 else 0

@Serializable
public data class TempData(
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

@PublishedApi
internal const val ErrorMessageLength: Int = 32

@PublishedApi
internal const val SERIALIZATION_EXCEPTION_SAVE: String = "xyz.cssxsh.weibo.json.save"

public suspend inline fun WeiboClient.text(
    url: String,
    crossinline block: HttpRequestBuilder.() -> Unit
): String {
    return useHttpClient { client -> client.prepareGet(url, block).body() }
}

public suspend inline fun <reified T> WeiboClient.temp(
    url: String,
    crossinline block: HttpRequestBuilder.() -> Unit
): T {
    val text = text(url, block)
    check(text.startsWith("{")) { text.substring(0, minOf(ErrorMessageLength, text.length)) }
    val temp = WeiboClient.Json.decodeFromString<TempData>(text)
    val data = requireNotNull(temp.data) {
        if (temp.url.orEmpty().startsWith(LOGIN_PAGE)) {
            "登陆状态无效，请登录"
        } else {
            text
        }
    }
    return try {
        WeiboClient.Json.decodeFromJsonElement(data)
    } catch (cause: SerializationException) {
        supervisorScope {
            System.getProperty(SERIALIZATION_EXCEPTION_SAVE)?.let { path ->
                val folder = java.io.File(path)
                folder.mkdirs()
                folder.resolve("${System.currentTimeMillis()}.json").writeText(text)
            }
        }
        throw IllegalStateException("${temp.httpCode} - ${temp.url}", cause)
    }
}

public suspend inline fun <reified T> WeiboClient.callback(
    url: String,
    crossinline block: HttpRequestBuilder.() -> Unit
): T {
    val json = text(url, block).substringAfter('(').substringBefore(')')
    return try {
        WeiboClient.Json.decodeFromString(json)
    } catch (cause: Exception) {
        throw IllegalArgumentException(json, cause)
    }
}

public suspend inline fun <reified T> WeiboClient.json(
    url: String,
    crossinline block: HttpRequestBuilder.() -> Unit
): T {
    val text = text(url, block)
    check(text.startsWith("{")) { text.substring(0, minOf(ErrorMessageLength, text.length)) }
    val temp = WeiboClient.Json.decodeFromString<TempData>(text)
    check(temp.ok) {
        if (temp.url.orEmpty().startsWith(LOGIN_PAGE)) {
            "登陆状态无效，请登录"
        } else {
            text
        }
    }
    return try {
        WeiboClient.Json.decodeFromString(text)
    } catch (cause: SerializationException) {
        supervisorScope {
            System.getProperty(SERIALIZATION_EXCEPTION_SAVE)?.let { path ->
                val folder = java.io.File(path)
                folder.mkdirs()
                folder.resolve("${System.currentTimeMillis()}.json").writeText(text)
            }
        }
        throw IllegalStateException("${temp.httpCode} - ${temp.url}", cause)
    }
}

public suspend fun WeiboClient.download(
    url: String,
    min: Long = 1024
): ByteArray = useHttpClient { client ->
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

public suspend fun WeiboClient.download(
    pid: String,
    index: Int
): ByteArray = useHttpClient { client ->
    client.prepareGet(image(pid = pid, server = ImageServer.random(), index = index)) {
        header(HttpHeaders.Referrer, INDEX_PAGE)
    }.body()
}

public suspend fun WeiboClient.download(
    video: PageInfo.PlayInfo
): Flow<ByteArray> = flow {
    for (offset in 0 until video.size step video.buffer) {
        val limit = (offset + video.buffer).coerceAtMost(video.size) - 1
        emit(useHttpClient { client ->
            client.prepareGet(video.url) {
                header(HttpHeaders.Referrer, INDEX_PAGE)
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
@PublishedApi
internal val Charsets.GBK: Charset
    get() = Charset.forName("GBK")

@PublishedApi
internal const val EncodeChars: String = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

public fun String.decodeBase62(): Long = fold(0L) { acc, char ->
    val index = EncodeChars.indexOf(char)
    check(index != -1) { "$char no 62" }
    acc * 62 + index
}

public const val WEIBO_EPOCH: Long = 515483463L

public fun timestamp(id: Long): Long = (id shr 22) + WEIBO_EPOCH

public fun id(mid: String): Long {
    return mid.substring(0..0).decodeBase62().times(1_0000000_0000000L) +
        mid.substring(1..4).decodeBase62().times(1_0000000L) +
        mid.substring(5..8).decodeBase62()
}

public val ImageServer: List<String> = listOf("wx1.sinaimg.cn", "wx2.sinaimg.cn", "wx3.sinaimg.cn", "wx4.sinaimg.cn")

public val ImageExtensions: Map<ContentType, String> = mapOf(
    ContentType.Image.JPEG to "jpg",
    ContentType.Image.GIF to "gif",
    ContentType.Image.PNG to "png",
)

public fun user(pid: String): Long = with(pid.substring(0..7)) {
    if (startsWith("00")) decodeBase62() else toLong(16)
}

public fun extension(pid: String): String = ImageExtensions.values.first { it.startsWith(pid[21]) }

public fun image(pid: String, server: String, index: Int): String = "https://${server}/large/${pid}.${extension(pid)}#${index}"

public fun picture(pid: String, index: Int): String = "https://weibo.com/ajax/common/download?pid=${pid}#${index}"

public val MicroBlog.link: String get() = "https://weibo.com/${user?.id ?: "detail"}/${mid}"

public val MicroBlog.username: String get() = user?.screen ?: "[未知用户]"

public val MicroBlog.uid: Long get() = user?.id ?: 0

public operator fun UserGroupData.get(id: String): UserGroup {
    for (category in groups) {
        for (group in category.list) {
            if (group.gid == id.toLongOrNull()) return group
            if (group.title == id) return group
        }
    }
    throw NoSuchElementException("Group: $id")
}
