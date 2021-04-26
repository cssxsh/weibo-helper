package xyz.cssxsh.mirai.plugin

import io.ktor.client.request.*
import io.ktor.http.*
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.jsoup.Jsoup
import xyz.cssxsh.mirai.plugin.WeiboHelperPlugin.logger
import xyz.cssxsh.mirai.plugin.WeiboHelperPlugin.client
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*
import xyz.cssxsh.weibo.data.feed.UserGroup
import java.io.File
import java.time.format.DateTimeFormatter

internal suspend fun SimpleMicroBlog.getContent(): String {
    return if (isLongText) {
        runCatching {
            requireNotNull(client.getLongText(id).data?.content) { "mid: $id" }
        }.getOrElse {
            logger.warning({ "获取微博[${id}]长文本失败" }, it)
            textRaw ?: Jsoup.parse(text).text()
        }
    } else {
        textRaw ?: Jsoup.parse(text).text()
    }
}

internal val Url.filename get() = encodedPath.substringAfterLast("/")

private const val CHAR_MIN = '0'.toLong()

internal fun String.toLong62() = fold(0L) { acc, c ->
    acc * 62 + (c.toLong() - CHAR_MIN)
}

internal fun Url.getUser(): Long = filename.substring(0..7).let {
    if (it.startsWith("00")) it.toLong62() else it.toLong(16)
}

internal const val WEIBO_EPOCH = 515483463L

internal fun timestamp(id: Long): Long = (id shr 22) + WEIBO_EPOCH

private suspend fun getWeiboImage(
    url: Url,
    name: String,
    refresh: Boolean = false
): File = File(WeiboHelperSettings.cachePath).resolve(name).apply {
    if (exists().not() || refresh) {
        parentFile.mkdirs()
        writeBytes(client.useHttpClient { it.get(url) })
    }
}

internal fun SimpleMicroBlog.getImageUrls() =
    pictureInfos.map { (_, picture) -> Url(picture.original.url) }

internal suspend fun SimpleMicroBlog.getImages() = getImageUrls().mapIndexed { index, url ->
    runCatching {
        getWeiboImage(
            url = url,
            name = "${createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE)}/${id}-${index}-${url.filename}"
        )
    }.onFailure {
        logger.warning({ "微博图片下载失败: $url" }, it)
    }
}

internal val SimpleMicroBlog.url get() = "https://weibo.com/detail/${id}"

internal val SimpleMicroBlog.username get() = user?.screen ?: "[未知用户]"

internal suspend fun SimpleMicroBlog.buildMessage(contact: Contact): MessageChain = buildMessageChain {
    appendLine("@${username}")
    appendLine("时间: $createdAt")
    appendLine("链接: $url")
    appendLine(getContent())
    runCatching {
        getImages().forEachIndexed { index, result ->
            result.onSuccess {
                append(it.uploadAsImage(contact))
            }.onFailure {
                logger.warning({ "获取微博[${id}]图片[${index}]失败" }, it)
                appendLine("获取微博[${id}]图片[${index}]失败")
            }
        }
    }.onFailure {
        logger.warning({ "获取微博[${id}]图片失败" }, it)
        appendLine("获取微博[${id}]图片失败")
    }

    retweeted?.let {
        appendLine("==============================")
        append(it.buildMessage(contact))
    }
}

internal fun UserBlogData.getMicroBlogs() = requireNotNull(data) { toString() }.list

internal fun UserGroupData.buildMessage(predicate: (UserGroup) -> Boolean = { true }) = buildMessageChain {
    groups.forEach { group ->
        group.list.filter(predicate).takeIf { it.isNotEmpty() }?.let { list ->
            appendLine("===${group.title}===")
            list.forEach { item ->
                appendLine("${item.title} -> ${item.gid}")
            }
        }
    }
}

internal fun UserGroupData.getGroup(id: Long) = requireNotNull(groups.flatMap { it.list }.find { it.gid == id })

internal fun UserInfoData.getUser() = requireNotNull(data) { toString() }.user