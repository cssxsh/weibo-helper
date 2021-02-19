package xyz.cssxsh.mirai.plugin

import io.ktor.client.request.*
import io.ktor.http.*
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.jsoup.Jsoup
import xyz.cssxsh.mirai.plugin.WeiboHelperPlugin.logger
import xyz.cssxsh.mirai.plugin.WeiboHelperPlugin.weiboClient
import xyz.cssxsh.mirai.plugin.data.WeiboHelperSettings.cachePath
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*
import java.io.File
import java.time.format.DateTimeFormatter

internal suspend fun SimpleMicroBlog.getContent(): String {
    return if (isLongText) {
        runCatching {
            requireNotNull(weiboClient.getLongText(id).data) { toString() }.longTextContent!!
        }.getOrElse {
            logger.warning({ "获取微博[${id}]长文本失败" }, it)
            textRaw ?: Jsoup.parse(text).text()
        }
    } else {
        textRaw ?: Jsoup.parse(text).text()
    }
}

internal fun Url.getFilename() = encodedPath.substring(encodedPath.lastIndexOfAny(listOf("\\", "/")) + 1)

private suspend fun getWeiboImage(
    url: Url,
    name: String,
    refresh: Boolean = false
): File = File(cachePath).resolve("${name}-${url.getFilename()}").apply {
    if (exists().not() || refresh) {
        parentFile.mkdirs()
        writeBytes(weiboClient.useHttpClient { it.get(url) })
    }
}

internal fun SimpleMicroBlog.getImageUrls() =
    pictureInfos.map { (_, picture) -> Url(picture.original.url) }

internal suspend fun SimpleMicroBlog.getImages() = getImageUrls().mapIndexed { index, url ->
    runCatching {
        getWeiboImage(url = url, name = "${createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE)}/${id}-${index}")
    }.onFailure {
        logger.warning({ "微博图片下载失败: $url" }, it)
    }
}

internal val SimpleMicroBlog.url
    get() = "https://weibo.com/detail/${id}"

internal val SimpleMicroBlog.username
    get() = user?.screenName ?: "[未知用户]"

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

internal fun MicroBlogData.getBlogs() =
    requireNotNull(data) { toString() }.list

internal fun UserGroupData.buildMessage(predicate: (UserGroupData.Item) -> Boolean = { true }) = buildMessageChain {
    groups.forEach { group ->
        group.list.filter(predicate).takeIf { it.isNotEmpty() }?.let {
            appendLine("===${group.title}===")
            it.forEach { item ->
                appendLine("${item.title} -> ${item.gid}")
            }
        }
    }
}
