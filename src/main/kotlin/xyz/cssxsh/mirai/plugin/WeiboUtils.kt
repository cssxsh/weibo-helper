package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.util.ContactUtils.getContactOrNull
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.sf.image4j.codec.ico.ICOEncoder
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*
import java.io.File
import java.net.URL
import java.time.YearMonth
import javax.imageio.ImageIO
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
    for (bot in Bot.instances) {
        return@lazy bot.getContactOrNull(WeiboHelperSettings.contact) ?: continue
    }
    return@lazy null
}

suspend fun MicroBlog.getContent(): String {
    return if (isLongText) {
        runCatching {
            requireNotNull(client.getLongText(id).content) { "mid: $id" }
        }.getOrElse {
            logger.warning({ "获取微博[${id}]长文本失败" }, it)
            raw ?: text
        }
    } else {
        raw ?: text
    }
}

internal fun File.desktop(user: UserBaseInfo) {
    mkdirs()
    resolve("desktop.ini").apply {
        if (isHidden) delete()
    }.writeText(buildString {
        appendLine("[.ShellClassInfo]")
        appendLine("LocalizedResourceName=${if (user.following) '$' else '#'}${user.id}@${user.screen}")
        if (user.following) {
            runCatching {
                ICOEncoder.write(ImageIO.read(URL(user.avatarLarge)), resolve("avatar.ico"))
            }
            appendLine("IconResource=avatar.ico")
        }
        appendLine("[ViewState]")
        appendLine("Mode=")
        appendLine("Vid=")
        appendLine("FolderType=Pictures")
    }, ChineseCharset)

    Runtime.getRuntime().exec("attrib $absolutePath +s")
}

internal suspend fun MicroBlog.getImages(flush: Boolean = false): List<Result<File>> = withContext(Dispatchers.IO) {
    if (pictures.isEmpty()) return@withContext emptyList()
    val cache = ImageCache.resolve("$uid").apply {
        if (resolve("desktop.ini").exists().not()) {
            desktop(requireNotNull(user))
        }
    }
    val last = created.toEpochSecond() * 1_000
    pictures.mapIndexed { index, pid ->
        runCatching {
            cache.resolve("${id}-${index}-${pid}.${extension(pid)}").apply {
                if (flush || !exists()) {
                    writeBytes(client.get(image(pid)))
                    setLastModified(last)
                }
            }
        }.onFailure {
            logger.warning({ "微博图片下载失败: $pid" }, it)
        }
    }
}

internal suspend fun MicroBlog.toMessage(contact: Contact): MessageChain = buildMessageChain {
    appendLine("@${username}")
    appendLine("时间: $createdAt")
    appendLine("链接: $link")
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

private val ImageClearInterval = (1).hours

internal fun CoroutineScope.clear(interval: Duration = ImageClearInterval) = launch {
    if (ImageExpire.isNegative().not()) return@launch
    while (isActive) {
        delay(interval)
        logger.info { "微博图片清理开始" }
        val last = System.currentTimeMillis() - ImageExpire.toLongMilliseconds()
        ImageCache.walkBottomUp().filter { file ->
            (file.extension in ImageExtensions.values) && file.lastModified() < last
        }.forEach { file ->
            runCatching {
                check(file.delete())
            }.onFailure {
                logger.info { "${file.absolutePath} 删除失败" }
            }
        }
    }
}