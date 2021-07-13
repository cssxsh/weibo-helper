package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.util.ContactUtils.getContactOrNull
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.utils.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.sf.image4j.codec.ico.ICOEncoder
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*
import java.io.File
import java.net.URL
import java.time.Duration
import java.time.YearMonth
import javax.imageio.ImageIO

internal val logger by WeiboHelperPlugin::logger

internal val client by WeiboHelperPlugin::client

internal val data by WeiboHelperPlugin::dataFolder

internal val ImageCache get() = File(WeiboHelperSettings.cache)

internal val ImageExpire get() = Duration.ofHours(WeiboHelperSettings.expire.toLong())

internal val IntervalFast get() = Duration.ofMinutes(WeiboHelperSettings.fast.toLong())

internal val IntervalSlow get() = Duration.ofMinutes(WeiboHelperSettings.slow.toLong())

internal val QuietGroups by WeiboHelperSettings::quiet

internal val LoginContact by lazy {
    for (bot in Bot.instances) {
        return@lazy bot.getContactOrNull(WeiboHelperSettings.contact) ?: continue
    }
    return@lazy null
}

internal val Emoticons by WeiboEmoticonData::emoticons

internal val EmoticonCache get() = ImageCache.resolve("emoticon")

internal fun File.desktop(user: UserBaseInfo) {
    mkdirs()
    resolve("desktop.ini").apply { if (isHidden) delete() }.writeText(buildString {
        appendLine("[.ShellClassInfo]")
        appendLine("LocalizedResourceName=${if (user.following) '$' else '#'}${user.id}@${user.screen}")
        if (user.following) {
            runCatching {
                ICOEncoder.write(ImageIO.read(URL(user.avatarLarge)), resolve("avatar.ico"))
            }.onFailure {
                logger.warning("头像下载失败", it)
            }
            appendLine("IconResource=avatar.ico")
        }
        appendLine("[ViewState]")
        appendLine("Mode=")
        appendLine("Vid=")
        appendLine("FolderType=Pictures")
    }, ChineseCharset)

    if (System.getProperty("os.name").lowercase().startsWith("windows")) {
        Runtime.getRuntime().exec("attrib $absolutePath +s")
    }
}

internal suspend fun Emoticon.file(): File {
    return EmoticonCache.resolve(category.ifBlank { "默认" }).resolve("$phrase.${url.substringAfterLast('.')}").apply {
        if (exists().not()) {
            parentFile.mkdirs()
            writeBytes(client.download(url))
        }
    }
}

internal suspend fun MicroBlog.getContent(links: List<UrlStruct> = urls) = supervisorScope {
    var content = raw
    if (isLongText) {
        runCatching {
            content = requireNotNull(client.getLongText(id).content) { "长文本为空 id: $id" }
        }.recoverCatching {
            content = requireNotNull(client.getLongText(mid).content) { "长文本为空 mid: $mid" }
        }.onFailure {
            logger.warning { "获取微博[${id}]长文本失败 $it" }
        }
    }
    links.fold(content.orEmpty()) { acc, struct ->
        acc.replace(struct.short, "[${struct.title}](${struct.long})")
    }
}

internal suspend fun MicroBlog.getImages(flush: Boolean = false): List<Result<File>> {
    if (pictures.isEmpty()) return emptyList()
    val cache = ImageCache.resolve("$uid").apply {
        if (resolve("desktop.ini").exists().not()) {
            desktop(requireNotNull(user))
        }
    }
    val last = created.toEpochSecond() * 1_000
    return pictures.mapIndexed { index, pid ->
        runCatching {
            cache.resolve("${id}-${index}-${pid}.${extension(pid)}").apply {
                if (flush || !exists()) {
                    writeBytes(runCatching {
                        client.download(image(pid))
                    }.recoverCatching {
                        client.download(download(pid))
                    }.recoverCatching {
                        client.download(image(pid).replace("large", "mw2000"))
                    }.onSuccess {
                        logger.info { "[${name}]下载完成, 大小${it.size / 1024}KB" }
                    }.getOrThrow())
                    setLastModified(last)
                }
            }
        }.onFailure {
            logger.warning { "微博图片下载失败: $pid, $it" }
        }
    }
}

private suspend fun MessageChainBuilder.parse(content: String, contact: Contact) {
    var pos = 0
    while (pos < content.length) {
        val start = content.indexOf('[', pos).takeIf { it != -1 } ?: break
        val emoticon = Emoticons.values.find { content.startsWith(it.phrase, start) }

        if (emoticon == null) {
            add(content.substring(pos, start + 1))
            pos = start + 1
            continue
        }

        runCatching {
            emoticon.file().uploadAsImage(contact)
        }.onSuccess {
            add(content.substring(pos, start))
            add(it)
        }.onFailure {
            logger.warning("获取微博表情[${emoticon.phrase}]图片失败, $it")
            add(content.substring(pos, start + emoticon.phrase.length))
        }
        pos = start + emoticon.phrase.length
    }
    appendLine(content.substring(pos))
}

internal suspend fun MicroBlog.toMessage(contact: Contact): MessageChain = buildMessageChain {
    appendLine("@${username}#${uid}")
    appendLine("时间: $created")
    appendLine("链接: $link")

    val content = getContent()

    if (Emoticons.isEmpty()) {
        appendLine(content)
    } else {
        parse(content, contact)
    }

    getImages().forEachIndexed { index, result ->
        result.mapCatching {
            add(it.uploadAsImage(contact))
        }.onFailure {
            logger.warning("获取微博[${id}]图片[${pictures[index]}]失败, $it")
            appendLine("获取微博[${id}]图片[${pictures[index]}]失败, $it")
        }
    }

    retweeted?.let {
        appendLine("==============================")
        add(it.copy(urls = urls).toMessage(contact))
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

internal fun CoroutineScope.clear(interval: Long = 1 * 60 * 60 * 1000) = launch(SupervisorJob()) {
    if (ImageExpire.isNegative.not()) return@launch
    while (isActive) {
        delay(interval)
        logger.info { "微博图片清理开始" }
        val last = System.currentTimeMillis() - ImageExpire.toMillis()
        ImageCache.walkBottomUp().filter { file ->
            if (file.nameWithoutExtension in Emoticons) return@filter false
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

internal suspend fun UserBaseInfo.getRecord(month: YearMonth, interval: Long) = supervisorScope {
    ImageCache.resolve("$id").apply {
        if (resolve("desktop.ini").exists().not()) {
            desktop(this@getRecord)
        }
    }.resolve("$month.json").run {
        if (exists() && month != YearMonth.now()) {
            WeiboClient.Json.decodeFromString(readText())
        } else {
            val blogs = runCatching {
                WeiboClient.Json.decodeFromString<List<MicroBlog>>(readText()).associateBy { it.id }.toMutableMap()
            }.getOrElse {
                mutableMapOf()
            }
            var page = 1
            var run = true
            while (isActive && run) {
                delay(interval)
                run = runCatching {
                    client.getUserMicroBlogs(uid = id, page = page, month = month).list
                }.onSuccess { list ->
                    blogs.putAll(list.associateBy { it.id })
                    logger.info { "@${screen}#${id}的${month}第${page}页加载成功" }
                    page++
                }.onFailure {
                    logger.warning({ "@${screen}#${id}的${month}第${page}页加载失败" }, it)
                }.getOrNull()?.size ?: Int.MAX_VALUE >= 16
            }
            blogs.values.toList().also {
                if (it.isNotEmpty()) {
                    writeText(WeiboClient.Json.encodeToString(it))
                }
            }
        }
    }
}

internal val ClientIgnore: suspend (Throwable) -> Boolean = { throwable ->
    WeiboClient.DefaultIgnore(throwable).also {
        if (it) logger.warning { "WeiboClient Ignore $throwable" }
    }
}

internal val SendLimit = """本群每分钟只能发\d+条消息""".toRegex()

internal const val SendDelay = 60 * 1000L

internal suspend fun <T : CommandSenderOnMessage<*>> T.sendMessage(block: suspend T.(Contact) -> Message): Boolean {
    return runCatching {
        block(fromEvent.subject)
    }.onSuccess { message ->
        quoteReply(message)
    }.onFailure {
        logger.warning {
            "发送消息失败, $it"
        }
        when {
            SendLimit.containsMatchIn(it.message.orEmpty()) -> {
                delay(SendDelay)
                quoteReply(SendLimit.find(it.message!!)!!.value)
            }
            else -> {
                quoteReply("发送消息失败， ${it.message}")
            }
        }
    }.isSuccess
}

suspend fun CommandSenderOnMessage<*>.quoteReply(message: Message) = sendMessage(fromEvent.message.quote() + message)

suspend fun CommandSenderOnMessage<*>.quoteReply(message: String) = quoteReply(message.toPlainText())

/**
 * 通过正负号区分群和用户
 */
val Contact.delegate get() = if (this is Group) id * -1 else id

/**
 * 查找Contact
 */
fun findContact(delegate: Long): Contact? {
    Bot.instances.forEach { bot ->
        if (delegate < 0) {
            bot.getGroup(delegate * -1)?.let { return@findContact it }
        } else {
            bot.getFriend(delegate)?.let { return@findContact it }
            bot.getStranger(delegate)?.let { return@findContact it }
            bot.groups.forEach { group ->
                group.getMember(delegate)?.let { return@findContact it }
            }
        }
    }
    return null
}