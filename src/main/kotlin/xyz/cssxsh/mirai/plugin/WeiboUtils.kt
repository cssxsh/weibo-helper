package xyz.cssxsh.mirai.plugin

import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.util.ContactUtils.getContactOrNull
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*
import java.io.File
import kotlin.time.*

internal val logger by WeiboHelperPlugin::logger

internal val client by WeiboHelperPlugin::client

internal val data by WeiboHelperPlugin::dataFolder

internal val ImageCache get() = File(WeiboHelperSettings.cache)

internal val ImageExpire get() = WeiboHelperSettings.expire.hours

internal val IntervalFast get() = WeiboHelperSettings.fast.minutes

internal val IntervalSlow get() = WeiboHelperSettings.slow.minutes

internal val QuietGroups by WeiboHelperSettings::quiet

internal val LoginContact by lazy {
    Bot.instances.forEach { bot ->
        return@lazy bot.getContactOrNull(WeiboHelperSettings.contact) ?: return@forEach
    }
    return@lazy null
}

suspend fun MicroBlog.getContent(): String {
    return if (continueTag != null) {
        runCatching {
            requireNotNull(client.getLongText(id).content) { "mid: $id" }
        }.getOrElse {
            logger.warning({ "获取微博[${id}]长文本失败" }, it)
            textRaw ?: text
        }
    } else {
        textRaw ?: text
    }
}

internal suspend fun MicroBlog.getImages() = images.mapIndexed { index, url ->
    runCatching {
        ImageCache.resolve("${date}/${id}-${index}-${url.filename}").apply {
            if (exists().not()) {
                parentFile.mkdirs()
                writeBytes(client.useHttpClient { it.get(url) })
            }
        }
    }.onFailure {
        logger.warning({ "微博图片下载失败: $url" }, it)
    }
}

internal suspend fun MicroBlog.toMessage(contact: Contact): MessageChain = buildMessageChain {
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
        append(it.toMessage(contact))
    }
}

private val GroupPredicate = { group: UserGroup -> group.type != UserGroupType.SYSTEM }

internal fun UserGroupData.toMessage(predicate: (UserGroup) -> Boolean = GroupPredicate) = buildMessageChain {
    groups.forEach { group ->
        group.list.filter(predicate).takeIf { it.isNotEmpty() }?.let { list ->
            appendLine("===${group.title}===")
            list.forEach { item ->
                appendLine("${item.title} -> ${item.gid}")
            }
        }
    }
}

private val ImageExtensions = listOf("jpg", "bmp", "png", "gif")

internal fun CoroutineScope.clear(interval: Duration = (1).hours) = launch {
    if (ImageExpire.isPositive().not()) return@launch
    while (isActive) {
        delay(interval)
        val last = System.currentTimeMillis() - ImageExpire.toLongMilliseconds()
        ImageCache.walkBottomUp().filter { file ->
            (file.extension in ImageExtensions) && file.lastModified() < last
        }.forEach { file ->
            runCatching {
                check(file.delete())
            }.onFailure {
                logger.info { "${file.absolutePath} 删除失败" }
            }
        }
    }
}