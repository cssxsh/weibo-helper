package xyz.cssxsh.mirai.plugin

import io.ktor.client.request.*
import io.ktor.http.*
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import xyz.cssxsh.mirai.plugin.WeiboHelperPlugin.logger
import xyz.cssxsh.mirai.plugin.WeiboHelperPlugin.client
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*
import java.io.File

internal val logger by WeiboHelperPlugin::logger

internal val client by WeiboHelperPlugin::client

private suspend fun getWeiboImage(
    url: Url,
    name: String,
    refresh: Boolean = false
): File = File(WeiboHelperSettings.cache).resolve(name).apply {
    if (exists().not() || refresh) {
        parentFile.mkdirs()
        writeBytes(client.useHttpClient { it.get(url) })
    }
}

internal suspend fun MicroBlog.getImages() = images.mapIndexed { index, url ->
    runCatching {
        getWeiboImage(url = url, name = "${date}/${id}-${index}-${url.filename}")
    }.onFailure {
        logger.warning({ "微博图片下载失败: $url" }, it)
    }
}

internal suspend fun MicroBlog.buildMessage(contact: Contact): MessageChain = buildMessageChain {
    appendLine("@${username}")
    appendLine("时间: $createdAt")
    appendLine("链接: $url")
    appendLine(getContent())

    getImages().forEachIndexed { index, result ->
        result.mapCatching {
            append(it.uploadAsImage(contact))
        }.onFailure {
            logger.warning({ "获取微博[${id}]图片[${index}]失败" }, it)
            appendLine("获取微博[${id}]图片[${index}]失败")
        }
    }

    retweeted?.let {
        appendLine("==============================")
        append(it.buildMessage(contact))
    }
}

private val GroupPredicate = { group: UserGroup -> group.type != UserGroupType.SYSTEM }

internal fun UserGroupData.buildMessage(predicate: (UserGroup) -> Boolean = GroupPredicate) = buildMessageChain {
    groups.forEach { group ->
        group.list.filter(predicate).takeIf { it.isNotEmpty() }?.let { list ->
            appendLine("===${group.title}===")
            list.forEach { item ->
                appendLine("${item.title} -> ${item.gid}")
            }
        }
    }
}

internal suspend fun WeiboClient.relogin(): LoginResult {
    val file = File(WeiboHelperSettings.cookies).apply {
        if (exists().not()) {
            parentFile.mkdirs()
            writeText("[]")
        }
    }
    loadCookies(readHttpCookie(file))
    return login()
}