package xyz.cssxsh.mirai.plugin

import io.ktor.client.*
import io.ktor.client.features.cookies.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*
import net.mamoe.mirai.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.console.util.*
import net.mamoe.mirai.console.util.ContactUtils.getContactOrNull
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.utils.*
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.RemoteFile.Companion.sendFile
import net.sf.image4j.codec.ico.*
import org.apache.commons.text.*
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*
import java.io.*
import java.net.*
import java.time.*
import javax.imageio.*

internal val logger by lazy {
    val open = System.getProperty("xyz.cssxsh.mirai.plugin.logger", "${true}").toBoolean()
    if (open) WeiboHelperPlugin.logger else SilentLogger
}

internal val client: WeiboClient by lazy {
    object : WeiboClient(ignore = ClientIgnore) {
        override var info: LoginUserInfo
            get() = super.info
            set(value) {
                WeiboStatusData.status = LoginStatus(value, cookies)
                super.info = value
            }

        override val timeout: Long get() = WeiboHelperSettings.timeout

        override val client: HttpClient = super.client.config {
            install(HttpCookies) {
                val delegate = super.storage
                storage = object : CookiesStorage by delegate {
                    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
                        delegate.addCookie(requestUrl, cookie)
                        WeiboStatusData.status = status()
                    }
                }
            }
        }

        init {
            load(WeiboStatusData.status)
        }
    }
}

internal fun AbstractJvmPlugin.registerPermission(name: String, description: String): Permission {
    return PermissionService.INSTANCE.register(permissionId(name), description, parentPermission)
}

internal val DataFolder by WeiboHelperPlugin::dataFolder

internal val ImageCache get() = File(WeiboHelperSettings.cache)

internal val ImageExpire get() = Duration.ofHours(WeiboHelperSettings.expire.toLong())

internal val ImageClearFollowing get() = WeiboHelperSettings.following

internal val IntervalFast get() = Duration.ofMinutes(WeiboHelperSettings.fast.toLong())

internal val IntervalSlow get() = Duration.ofMinutes(WeiboHelperSettings.slow.toLong())

internal val PictureCount by WeiboHelperSettings::pictures

@OptIn(ConsoleExperimentalApi::class)
internal val LoginContact by lazy {
    for (bot in Bot.instances) {
        return@lazy bot.getContactOrNull(WeiboHelperSettings.contact) ?: continue
    }
    return@lazy null
}

internal val Emoticons by WeiboEmoticonData::emoticons

internal val EmoticonCache get() = ImageCache.resolve("emoticon")

internal val VideoCache get() = ImageCache.resolve("video")

internal val CoverCache get() = ImageCache.resolve("cover")

internal val HistoryExpire get() = WeiboHelperSettings.history

typealias BuildMessage = suspend (contact: Contact) -> Message

internal fun UserBaseInfo.desktop(flush: Boolean = false, dir: File = ImageCache.resolve("$id")): File {
    dir.mkdirs()
    if (!flush
        || dir.resolve("desktop.ini").exists()
        || (following && dir.resolve("avatar.ico").exists())
    ) return dir

    dir.resolve("desktop.ini").apply { if (isHidden) dir.delete() }.writeText(buildString {
        appendLine("[.ShellClassInfo]")
        appendLine("LocalizedResourceName=${if (following) '$' else '#'}${id}@${screen}")
        if (following) {
            try {
                ICOEncoder.write(ImageIO.read(URL(avatarLarge)), dir.resolve("avatar.ico"))
            } catch (e: Throwable) {
                logger.warning("头像下载失败", e)
            }
            appendLine("IconResource=avatar.ico")
        }
        appendLine("[ViewState]")
        appendLine("Mode=")
        appendLine("Vid=")
        appendLine("FolderType=Pictures")
    }, Charsets.GBK)

    if (System.getProperty("os.name").lowercase().startsWith("windows")) {
        Runtime.getRuntime().exec("attrib ${dir.absolutePath} +s")
    }

    return dir
}

internal suspend fun Emoticon.file(): File {
    return EmoticonCache.resolve(category.ifBlank { "默认" }).resolve("$phrase.${url.substringAfterLast('.')}").apply {
        if (exists().not()) {
            parentFile.mkdirs()
            writeBytes(client.download(url))
        }
    }
}

internal suspend fun MicroBlog.getContent() = supervisorScope {
    var content = raw
    var links = urls
    if (isLongText) {
        try {
            val data = client.getLongText(mid)
            content = requireNotNull(data.content) { "长文本为空 mid: $mid" }
            links = data.urls
        } catch (e: Throwable) {
            logger.warning { "获取微博[${id}]长文本失败 $e" }
        }
    }
    links.fold(StringEscapeUtils.unescapeHtml4(content).orEmpty()) { acc, struct ->
        if (struct.long.isBlank()) return@fold acc
        acc.replace(struct.short, "[${struct.title}]<${struct.type}>(${struct.long})")
    }
}

internal suspend fun MicroBlog.getImages(flush: Boolean = false) = supervisorScope {
    if (pictures.isEmpty()) return@supervisorScope emptyList()
    val user = requireNotNull(user) { "没有用户信息" }
    val cache = user.desktop()
    val last = created.toEpochSecond() * 1_000

    pictures.mapIndexed { index, pid ->
        async {
            cache.resolve("${id}-${index}-${pid}.${extension(pid)}").apply {
                if (flush || exists().not()) {
                    writeBytes(runCatching {
                        // 下载速度更快
                        client.download(download(pid) + "#$index")
                    }.recoverCatching {
                        client.download(image(pid) + "#$index")
                    }.onSuccess {
                        logger.verbose { "[${name}]下载完成, 大小${it.size / 1024}KB" }
                    }.getOrThrow())
                    setLastModified(last)
                }
            }
        }
    }
}

internal suspend fun MicroBlog.getVideo(flush: Boolean = false) = supervisorScope {
    val media = requireNotNull(page?.media) { "MicroBlog(${mid}) Not Found Video" }
    val title = media.titles.firstOrNull()?.title ?: media.title
    val video = media.playbacks.first().info

    VideoCache.resolve("${id}-${title}.mp4").apply {
        if (flush || exists().not()) {
            parentFile.mkdirs()
            client.download(video = video).collect(::appendBytes)
        }
    }
}

internal suspend fun MicroBlog.getCover(flush: Boolean = false) = supervisorScope {
    val url = page?.picture ?: return@supervisorScope null

    CoverCache.resolve(url.substringAfterLast("/")).apply {
        if (flush || exists().not()) {
            parentFile.mkdirs()
            writeBytes(client.download(url = url))
        }
    }
}

private suspend fun emoticon(content: String, contact: Contact) = buildMessageChain {
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
            logger.warning("获取微博表情${emoticon.phrase}图片失败, $it")
            add(content.substring(pos, start + emoticon.phrase.length))
        }
        pos = start + emoticon.phrase.length
    }
    appendLine(content.substring(pos))
}

internal suspend fun MicroBlog.toMessage(contact: Contact): MessageChain = buildMessageChain {
    appendLine("@${username}#${uid}")
    title?.run { appendLine("标题: $text") }
    appendLine("时间: $created")
    appendLine("链接: $link")
    suffix?.run { appendLine(joinToString(" ") { it.content }) }

    // FIXME: Send Video
    if (hasVideo) {
        supervisorScope {
            launch {
                val file = getVideo()
                if (contact is FileSupported) {
                    contact.sendFile(file.name, file)
                } else {
                    logger.warning { "$contact 无法发送文件" }
                }
            }
        }
    }

    val content = getContent()

    if (Emoticons.isEmpty()) {
        appendLine(content)
    } else {
        add(emoticon(content, contact))
    }

    if (PictureCount < 0 || pictures.size <= PictureCount) {
        for ((index, deferred) in getImages().withIndex()) {
            deferred.runCatching {
                add(await().uploadAsImage(contact))
            }.onFailure {
                logger.warning("获取微博[${id}]图片[${pictures[index]}]失败, $it")
                appendLine("获取微博[${id}]图片[${pictures[index]}]失败, $it")
            }
        }
    } else if (pictures.size > PictureCount) {
        appendLine("图片过多，已省略")
    }

    getCover()?.runCatching {
        add(uploadAsImage(contact))
    }?.onFailure {
        logger.warning("获取微博[${id}]封面失败, $it")
        appendLine("获取微博[${id}]封面失败, $it")
    }

    retweeted?.let { blog ->
        appendLine("======================")
        add(blog.copy(urls = urls).toMessage(contact))
    }
}

private val GroupPredicate = { group: UserGroup -> group.type != UserGroupType.SYSTEM }

internal fun UserGroupData.toMessage(predicate: (UserGroup) -> Boolean = GroupPredicate) = buildMessageChain {
    for (group in groups) {
        val list = group.list.filter(predicate)
        if (list.isNotEmpty()) {
            appendLine("===${group.title}===")
        }
        for (item in list) {
            appendLine("${item.title} -> ${item.gid}")
        }
    }
}

internal suspend fun UserInfo.toMessage(contact: Contact) = buildMessageChain {
    append(client.download(avatarLarge).toExternalResource().use { it.uploadAsImage(contact) })
    appendLine("已关注 @${screen}#${id}")
}

internal fun File.clean(following: Boolean, num: Int = 0) {
    logger.info { "微博图片清理开始" }
    val last = System.currentTimeMillis() - ImageExpire.toMillis()
    for (dir in listFiles().orEmpty()) {
        val avatar = dir.resolve("avatar.ico").exists()
        if (following.not() && avatar) continue
        val images = dir.listFiles { file -> file.extension in ImageExtensions.values }.orEmpty()
        if (num > 0 && images.size > num) continue
        images.all { file -> file.lastModified() < last && file.delete() }
            && dir.apply { for (file in listFiles().orEmpty()) file.delete() }.delete()
    }
}

internal suspend fun clear(interval: Long = 3600_000) = supervisorScope {
    if (ImageExpire.isNegative) return@supervisorScope
    while (isActive) {
        ImageCache.clean(following = ImageClearFollowing)
        delay(interval)
    }
}

@OptIn(ExperimentalSerializationApi::class)
internal suspend fun UserBaseInfo.getRecord(month: YearMonth, interval: Long) = supervisorScope {
    with(desktop(true).resolve("$month.json")) {
        if (exists() && month != YearMonth.now()) {
            WeiboClient.Json.decodeFromString(readText())
        } else {
            val blogs = try {
                WeiboClient.Json.decodeFromString<List<MicroBlog>>(readText()).associateBy { it.id }.toMutableMap()
            } catch (e: Throwable) {
                mutableMapOf()
            }
            var page = 1
            var run = true
            while (isActive && run) {
                delay(interval)
                run = (runCatching {
                    client.getUserMicroBlogs(uid = id, page = page, month = month).list
                }.onSuccess { list ->
                    blogs.putAll(list.associateBy { it.id })
                    logger.info { "@${screen}#${id}的${month}第${page}页加载成功" }
                    page++
                }.onFailure {
                    logger.warning({ "@${screen}#${id}的${month}第${page}页加载失败" }, it)
                }.getOrNull()?.size ?: Int.MAX_VALUE) >= 16
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

internal suspend fun WeiboClient.init() = supervisorScope {
    runCatching {
        restore()
    }.onSuccess {
        logger.info { "登陆成功, $it" }
    }.onFailure {
        logger.warning { "登陆失败, ${it.message}, 请尝试使用 /wlogin 指令登录" }
        runCatching {
            incarnate()
        }.onSuccess {
            logger.info { "模拟游客成功，置信度${it}" }
        }.onFailure {
            logger.warning { "模拟游客失败, ${it.message}" }
        }
    }.isSuccess && runCatching {
        getEmoticon().emoticon.let { map ->
            (map.brand.values + map.usual + map.more).flatMap { it.values.flatten() }.associateBy {
                it.phrase
            }.let {
                Emoticons.putAll(it)
            }
        }
    }.onSuccess {
        logger.info { "加载表情成功" }
    }.onFailure {
        logger.warning { "加载表情失败, $it" }
    }.isSuccess
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
    for (bot in Bot.instances) {
        if (delegate < 0) {
            for (group in bot.groups) {
                if (group.id == delegate * -1) return group
            }
        } else {
            for (friend in bot.friends) {
                if (friend.id == delegate) return friend
            }
            for (stranger in bot.strangers) {
                if (stranger.id == delegate) return stranger
            }
            for (friend in bot.friends) {
                if (friend.id == delegate) return friend
            }
            for (group in bot.groups) {
                for (member in group.members) {
                    if (member.id == delegate) return member
                }
            }
        }
    }
    return null
}