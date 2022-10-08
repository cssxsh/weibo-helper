package xyz.cssxsh.mirai.weibo

import io.ktor.client.*
import io.ktor.client.plugins.cookies.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import net.mamoe.mirai.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.console.util.*
import net.mamoe.mirai.console.util.ContactUtils.getContactOrNull
import net.mamoe.mirai.console.util.ContactUtils.render
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.utils.*
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.sf.image4j.codec.ico.*
import org.apache.commons.text.*
import xyz.cssxsh.mirai.weibo.data.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*
import java.io.*
import java.net.*
import java.time.*
import javax.imageio.*

internal const val WEIBO_CACHE_PROPERTY = "xyz.cssxsh.mirai.plugin.weibo.cache"

internal const val WEIBO_EXPIRE_IMAGE_PROPERTY = "xyz.cssxsh.mirai.plugin.weibo.expire.image"

internal const val WEIBO_EXPIRE_HISTORY_PROPERTY = "xyz.cssxsh.mirai.plugin.weibo.expire.history"

internal const val WEIBO_CLEAN_FOLLOWING_PROPERTY = "xyz.cssxsh.mirai.plugin.clean.following"

internal const val WEIBO_INTERVAL_FAST_PROPERTY = "xyz.cssxsh.mirai.plugin.weibo.interval.fast"

internal const val WEIBO_INTERVAL_SLOW_PROPERTY = "xyz.cssxsh.mirai.plugin.weibo.interval.slow"

internal const val WEIBO_CONTACT_PROPERTY = "xyz.cssxsh.mirai.plugin.weibo.contact"

internal const val WEIBO_FORWARD_PROPERTY = "xyz.cssxsh.mirai.plugin.weibo.forward"

internal const val WEIBO_URL_PROPERTY = "xyz.cssxsh.mirai.plugin.weibo.url"

/**
 * @see [WeiboHelperPlugin.logger]
 */
internal val logger by lazy {
    try {
        WeiboHelperPlugin.logger
    } catch (_: ExceptionInInitializerError) {
        MiraiLogger.Factory.create(WeiboClient::class)
    }
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

/**
 * @see [WEIBO_CACHE_PROPERTY]
 * @see [WeiboHelperSettings.cache]
 */
internal val ImageCache: File by lazy {
    File(System.getProperty(WEIBO_CACHE_PROPERTY, WeiboHelperSettings.cache))
}

/**
 * @see [WEIBO_EXPIRE_IMAGE_PROPERTY]
 * @see [WeiboHelperSettings.expire]
 */
internal val ImageExpire: Duration by lazy {
    Duration.ofHours(System.getProperty(WEIBO_EXPIRE_IMAGE_PROPERTY)?.toLong() ?: WeiboHelperSettings.expire.toLong())
}

/**
 * @see [WEIBO_CLEAN_FOLLOWING_PROPERTY]
 * @see [WeiboHelperSettings.following]
 */
internal val ImageClearFollowing: Boolean by lazy {
    System.getProperty(WEIBO_CLEAN_FOLLOWING_PROPERTY)?.toBoolean() ?: WeiboHelperSettings.following
}

/**
 * @see [WEIBO_INTERVAL_FAST_PROPERTY]
 * @see [WeiboHelperSettings.fast]
 */
internal val IntervalFast: Duration by lazy {
    Duration.ofMinutes(System.getProperty(WEIBO_INTERVAL_FAST_PROPERTY)?.toLong() ?: WeiboHelperSettings.fast.toLong())
}

/**
 * @see [WEIBO_INTERVAL_SLOW_PROPERTY]
 * @see [WeiboHelperSettings.slow]
 */
internal val IntervalSlow: Duration by lazy {
    Duration.ofMinutes(System.getProperty(WEIBO_INTERVAL_SLOW_PROPERTY)?.toLong() ?: WeiboHelperSettings.slow.toLong())
}

/**
 * @see [WEIBO_CONTACT_PROPERTY]
 * @see [WeiboHelperSettings.contact]
 */
@OptIn(ConsoleExperimentalApi::class)
internal val LoginContact by lazy {
    val id = System.getProperty(WEIBO_CONTACT_PROPERTY)?.toLong() ?: WeiboHelperSettings.contact
    for (bot in Bot.instances) {
        return@lazy bot.getContactOrNull(id) ?: continue
    }
    throw NoSuchElementException("Not Found LoginContact $id")
}

internal fun sendLoginMessage(message: String) {
    try {
        WeiboHelperPlugin
    } catch (_: Throwable) {
        CoroutineScope(Dispatchers.IO)
    }.launch(SupervisorJob()) {
        while (isActive) {
            try {
                LoginContact.sendMessage(message)
                break
            } catch (cause: Throwable) {
                logger.warning({ "向 ${LoginContact.render()} 发送消息失败" }, cause)
            }
            delay(60_000L)
        }
    }
}

internal val Emoticons get() = WeiboEmoticonData.emoticons

internal val EmoticonCache get() = ImageCache.resolve("emoticon")

internal val VideoCache get() = ImageCache.resolve("video")

internal val CoverCache get() = ImageCache.resolve("cover")

/**
 * @see [WEIBO_EXPIRE_HISTORY_PROPERTY]
 * @see [WeiboHelperSettings.history]
 */
internal val HistoryExpire: Long by lazy {
    System.getProperty(WEIBO_EXPIRE_HISTORY_PROPERTY)?.toLong() ?: WeiboHelperSettings.history
}

/**
 * @see [WEIBO_FORWARD_PROPERTY]
 * @see [WeiboHelperSettings.forward]
 */
internal val UseForwardMessage: Boolean by lazy {
    System.getProperty(WEIBO_FORWARD_PROPERTY)?.toBoolean() ?: WeiboHelperSettings.forward
}

/**
 * @see [WEIBO_URL_PROPERTY]
 * @see [WeiboHelperSettings.showUrl]
 */
internal val ShowUrl: Boolean by lazy {
    System.getProperty(WEIBO_URL_PROPERTY)?.toBoolean() ?: WeiboHelperSettings.showUrl
}

internal fun UserBaseInfo.desktop(flush: Boolean = false, dir: File = ImageCache.resolve("$id")): File {
    dir.mkdirs()
    if (!flush
        || dir.resolve("desktop.ini").exists()
        || (following && dir.resolve("avatar.ico").exists())
    ) return dir

    dir.resolve("desktop.ini").apply { if (isHidden) dir.deleteRecursively() }.writeText(buildString {
        appendLine("[.ShellClassInfo]")
        appendLine("LocalizedResourceName=${if (following) '$' else '#'}${id}@${screen}")
        if (following) {
            try {
                ICOEncoder.write(ImageIO.read(URL(avatarLarge)), dir.resolve("avatar.ico"))
            } catch (cause: Exception) {
                logger.warning({ "头像下载失败" }, cause)
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

internal suspend fun MicroBlog.getContent(url: Boolean = true) = supervisorScope {
    var content = raw
    var links = urls
    if (isLongText) {
        try {
            val data = client.getLongText(mid)
            content = requireNotNull(data.content) { "长文本为空 mid: $mid" }
            links = data.urls
        } catch (cause: Exception) {
            logger.warning({ "获取微博[${id}]长文本失败" }, cause)
        }
    }
    links.fold(StringEscapeUtils.unescapeHtml4(content).orEmpty()) { acc, struct ->
        if (struct.long.isBlank()) return@fold acc
        if (url) {
            acc.replace(struct.short, "[${struct.title}]<${struct.type}>(${struct.long})")
        } else {
            acc.replace(struct.short, "[${struct.title}]")
        }
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
                        client.download(picture(pid, index))
                    }.recoverCatching {
                        client.download(pid, index)
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
    val title = media.titles.firstOrNull()?.title ?: media.name
    val video = media.playbacks.maxOf { it.info }

    VideoCache.resolve("${id}-${title}.mp4").apply {
        if (flush || exists().not()) {
            parentFile.mkdirs()
            client.download(video = video).collect(::appendBytes)
        }
    }
}

internal suspend fun MicroBlog.getCover(flush: Boolean = false) = supervisorScope {
    val url = requireNotNull(page?.picture) { "MicroBlog(${mid}) Not Found Cover" }

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
        val start = content.indexOf('[', pos)
        if (start < 0) break
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
            logger.warning({ "获取微博表情${emoticon.phrase}图片失败" }, it)
            add(content.substring(pos, start + emoticon.phrase.length))
        }
        pos = start + emoticon.phrase.length
    }
    appendLine(content.substring(pos))
}

internal suspend fun MicroBlog.toMessage(contact: Contact): MessageChain = buildMessageChain {
    title?.run { appendLine(text) }
    appendLine("@${username}#${uid}")
    appendLine("时间: $created")
    appendLine(if (ShowUrl) "链接: $link" else "MID: $mid")
    suffix?.run { appendLine(joinToString(" ") { it.content }) }
    appendLine("\uD83D\uDCAC: $comments \uD83D\uDD01: $reposts \uD83D\uDC4D\uD83C\uDFFB: $attitudes")

    // FIXME: Send Video
    if (WeiboHelperSettings.video && hasVideo) {
        supervisorScope {
            launch {
                try {
                    val file = getVideo()
                    contact as FileSupported
                    file.toExternalResource().use { contact.files.uploadNewFile(file.name, it) }
                } catch (cause: Exception) {
                    logger.warning({ "$contact 无法发送文件" }, cause)
                }
            }
        }
    }

    val content = getContent(url = ShowUrl)

    if (WeiboHelperSettings.emoticon && Emoticons.isNotEmpty()) {
        add(emoticon(content, contact))
    } else {
        appendLine(content)
    }

    when (val picture = WeiboHelperSettings.picture) {
        is WeiboPicture.None -> Unit
        is WeiboPicture.All -> {
            for ((index, deferred) in getImages().withIndex()) {
                try {
                    add(deferred.await().uploadAsImage(contact))
                } catch (cause: Exception) {
                    logger.warning({ "获取微博[${id}]图片[${pictures[index]}]失败" }, cause)
                    appendLine("获取微博[${id}]图片[${pictures[index]}]失败")
                }
            }
        }
        is WeiboPicture.Limit -> {
            for ((index, deferred) in getImages().withIndex()) {
                if (picture.total <= index) {
                    appendLine("超过${picture.total}, 剩余图片省略")
                    break
                }
                try {
                    add(deferred.await().uploadAsImage(contact))
                } catch (cause: Exception) {
                    logger.warning({ "获取微博[${id}]图片[${pictures[index]}]失败" }, cause)
                    appendLine("获取微博[${id}]图片[${pictures[index]}]失败")
                }
            }
        }
        is WeiboPicture.Top -> {
            if (picture.total < pictures.size) {
                for ((index, deferred) in getImages().withIndex()) {
                    try {
                        add(deferred.await().uploadAsImage(contact))
                    } catch (cause: Exception) {
                        logger.warning({ "获取微博[${id}]图片[${pictures[index]}]失败" }, cause)
                        appendLine("获取微博[${id}]图片[${pictures[index]}]失败")
                    }
                }
            } else {
                appendLine("超过${picture.total}, 图片省略")
            }
        }
    }

    if (WeiboHelperSettings.cover && hasPage) {
        try {
            add(getCover().uploadAsImage(contact))
        } catch (cause: Exception) {
            logger.warning({ "获取微博[${id}]封面失败" }, cause)
            appendLine("获取微博[${id}]封面失败")
        }
    }

    retweeted?.let { blog ->
        appendLine("======================")
        add(blog.copy(urls = urls).toMessage(contact))
    }
}

private val NoDefault = { group: UserGroup -> group.type != UserGroupType.SYSTEM }

internal fun UserGroupData.toMessage(predicate: (UserGroup) -> Boolean = NoDefault) = buildMessageChain {
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
    for (dir in listFiles() ?: return) {
        val avatar = dir.resolve("avatar.ico").exists()
        if (following.not() && avatar) continue
        val images = dir.listFiles { file -> file.extension in ImageExtensions.values } ?: continue
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

internal suspend fun restore(interval: Long = 600_000) = supervisorScope {
    while (isActive) {
        val timestamp = client.wbpsess?.expires?.timestamp
        if (timestamp != null) {
            delay((timestamp - System.currentTimeMillis()).coerceAtMost(interval))
            continue
        }
        try {
            val result = client.restore()
            logger.info { "WEIBO登陆状态已刷新 $result" }
            continue
        } catch (throwable: SerializationException) {
            logger.warning({ "WEIBO RESTORE 任务序列化时失败" }, throwable)
            sendLoginMessage("WEIBO RESTORE 任务序列化时失败")
        } catch (cause: Throwable) {
            logger.warning({ "WEIBO登陆状态失效，需要重新登陆" }, cause)
            sendLoginMessage("WEIBO登陆状态失效，需要重新登陆 /wlogin")
        }
        delay(interval)
    }
}

internal suspend fun UserBaseInfo.getRecord(month: YearMonth, interval: Long) = supervisorScope {
    with(desktop(true).resolve("$month.json")) {
        if (exists() && month != YearMonth.now()) {
            WeiboClient.Json.decodeFromString(readText())
        } else {
            val blogs = try {
                WeiboClient.Json.decodeFromString<List<MicroBlog>>(readText())
                    .associateByTo(HashMap()) { it.id }
            } catch (cause: Exception) {
                hashMapOf()
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
            val list = blogs.values.toList()
            if (list.isNotEmpty()) {
                writeText(WeiboClient.Json.encodeToString(list))
            }
            list
        }
    }
}

internal val ClientIgnore: suspend (Throwable) -> Boolean = { throwable ->
    when (throwable) {
        is UnknownHostException,
        is NoRouteToHostException -> false
        is okhttp3.internal.http2.StreamResetException -> true
        is IOException -> {
            logger.warning { "WeiboClient Ignore $throwable" }
            true
        }
        else -> false
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
        val emoticon = getEmoticon().emoticon
        for (map in (emoticon.brand.values + emoticon.usual + emoticon.more)) {
            for ((_, list) in map) {
                Emoticons.putAll(list.associateBy { it.phrase })
            }
        }
    }.onSuccess {
        logger.info { "加载表情成功" }
    }.onFailure {
        logger.warning { "加载表情失败, $it" }
    }.isSuccess
}

internal suspend fun <T : CommandSenderOnMessage<*>> T.quote(block: suspend T.(Contact) -> Message): Boolean {
    return try {
        quoteReply(block(fromEvent.subject))
        true
    } catch (cause: Throwable) {
        logger.warning({ "发送消息失败" }, cause)
        quoteReply("发送消息失败， ${cause.message}")
        false
    }
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