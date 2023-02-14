package xyz.cssxsh.mirai.weibo

import kotlinx.coroutines.*
import kotlinx.serialization.*
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.weibo.data.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.data.*
import xyz.cssxsh.weibo.api.*
import java.net.*
import java.time.*
import kotlin.coroutines.*

public abstract class WeiboSubscriber<K : Comparable<K>>(public val type: String) : CoroutineScope {

    override val coroutineContext: CoroutineContext =
        CoroutineName(name = "WeiboListener-$type") + SupervisorJob() + CoroutineExceptionHandler { context, throwable ->
            logger.warning({ "$throwable in $context" }, throwable)
        }

    public companion object {
        private val all = mutableListOf<WeiboSubscriber<*>>()

        public fun start() {
            for (subscriber in all) {
                subscriber.start()
            }
        }

        public fun stop() {
            for (subscriber in all) {
                subscriber.stop()
            }
        }
    }

    init {
        let(all::add)
    }

    public abstract val load: suspend (id: K) -> List<MicroBlog>

    protected open val filter: WeiboFilter get() = WeiboHelperSettings

    private val forward get() = UseForwardMessage

    protected abstract val tasks: MutableMap<K, WeiboTaskInfo>

    private val taskJobs: MutableMap<K, Job> = HashMap()

    private fun infos(id: K) = tasks[id]?.contacts.orEmpty()

    public fun start(): Unit = synchronized(taskJobs) {
        for ((id, _) in tasks) {
            taskJobs[id] = listen(id)
        }
    }

    public fun stop(): Unit = synchronized(taskJobs) {
        coroutineContext.cancelChildren()
        taskJobs.clear()
    }

    private suspend fun sendMessageToTaskContacts(id: K, build: suspend (contact: Contact) -> Message) {
        for (delegate in infos(id)) {
            try {
                requireNotNull(findContact(delegate)) { "找不到用户" }.let { contact ->
                    contact.sendMessage(build(contact))
                }
            } catch (cause: Exception) {
                logger.warning({ "对[${delegate}]构建消息失败" }, cause)
            }
        }
    }

    private operator fun LocalTime.minus(other: LocalTime): Duration =
        Duration.ofSeconds((toSecondOfDay() - other.toSecondOfDay()).toLong())

    private fun Map<Long, MicroBlog>.near(time: LocalTime = LocalTime.now()): Boolean {
        return values.map { it.created.toLocalTime() - time }.any { it.abs() < IntervalSlow }
    }

    protected open val reposts: Boolean = true

    protected open val predicate: (MicroBlog, K) -> Boolean = filter@{ blog, id ->
        if (filter.original && blog.retweeted != null) {
            logger.debug { "${type}(${id}) 转发屏蔽" }
            return@filter false
        }
        val source = blog.retweeted ?: blog
        if (reposts && source.reposts < filter.repost) {
            logger.debug { "${type}(${id}) 转发数屏蔽，跳过 ${source.id} ${source.reposts}" }
            return@filter false
        }
        if (source.uid in filter.users) {
            logger.info { "${type}(${id}) 用户屏蔽，跳过 ${source.id} ${source.username}" }
            return@filter false
        }
        for (regex in filter.regexes) {
            if (regex in source.raw.orEmpty()) {
                logger.info { "${type}(${id}) 正则屏蔽，跳过 ${source.id} $regex" }
                return@filter false
            }
        }
        if (blog.urls.any { it.type.toIntOrNull() in filter.urls }) {
            logger.debug { "${type}(${id}) Url屏蔽，跳过 ${source.id} ${blog.urls}" }
            return@filter false
        }
        if (blog.title != null && "赞过的微博" in blog.title.text) {
            logger.info { "${type}(${id}) 赞过的微博屏蔽，跳过 ${source.id} ${source.created}" }
            return@filter false
        }
        true
    }

    private fun listen(id: K): Job = launch {
        logger.info { "添加对$type(${tasks.getValue(id).name}#${id})的监听任务" }
        val history by WeiboHistoryDelegate(id, this@WeiboSubscriber)
        val cache: MutableSet<Long> = HashSet(history.keys)
        for ((_, blog) in history) cache.add(blog.retweeted?.id ?: continue)
        logger.debug { "$type(${tasks.getValue(id).name}#${id})的 cache: $cache" }
        var init = true
        logger.debug { "$type(${tasks.getValue(id).name}#${id})的 target: ${infos(id)}" }
        while (isActive && infos(id).isNotEmpty()) {
            delay((if (history.near() || init) IntervalFast else IntervalSlow).toMillis())
            try {
                if (init) {
                    // XXX: 加载一次
                    val list = load(id)
                    for (blog in list) {
                        history[blog.id] = blog
                        cache.add(blog.id)
                        cache.add(blog.retweeted?.id ?: continue)
                    }
                    init = false
                    logger.debug { "$type(${tasks.getValue(id).name}#${id})的 init: $cache" }
                    continue
                }
                val task = tasks.getValue(id)
                val list = load(id).asSequence()
                    .filter { predicate(it, id) }
                    .filterNot { (it.retweeted?.id ?: it.id) in cache }
                    .toList()
                if (list.isEmpty()) continue

                if (forward) {
                    val strategy = object : ForwardMessage.DisplayStrategy {
                        override fun generateTitle(forward: RawForwardMessage): String = "${task.name} 有新微博"
                        override fun generateSummary(forward: RawForwardMessage): String = "查看${list.size}条微博转发"
                    }
                    sendMessageToTaskContacts(id) { contact ->
                        buildForwardMessage(contact) {
                            displayStrategy = strategy
                            for (blog in list) {
                                contact.bot at blog.created.toEpochSecond().toInt() says blog.toMessage(contact)
                            }
                        }
                    }
                } else {
                    for (blog in list) {
                        sendMessageToTaskContacts(id) { contact ->
                            blog.toMessage(contact)
                        }
                    }
                }

                var last = task.last
                for (blog in list) {
                    history[blog.id] = blog
                    last = maxOf(blog.created, last)
                    cache.add(blog.id)
                    cache.add(blog.retweeted?.id ?: continue)
                }

                tasks[id] = task.copy(last = last)
            } catch (exception: SerializationException) {
                logger.warning({ "$type(${id})监听任务序列化时失败" }, exception)
                sendLoginMessage("$type(${id})监听任务序列化时失败")
            } catch (exception: UnknownHostException) {
                logger.warning({ "$type(${id})监听任务, 网络异常" }, exception)
            } catch (exception: SocketException) {
                logger.warning({ "$type(${id})监听任务, 网络异常" }, exception)
            } catch (exception: Exception) {
                logger.warning({ "WEIBO登陆状态将刷新" }, exception)
                try {
                    client.restore()
                } catch (_: Exception) {
                    //
                }
            } finally {
                logger.debug { "$type(${id}): ${tasks[id]}监听任务完成一次, 即将进入延时" }
            }
        }
    }

    public fun add(id: K, name: String, subject: Contact): Unit = synchronized(tasks) {
        tasks.compute(id) { _, info ->
            with(info ?: WeiboTaskInfo()) {
                copy(contacts = contacts + subject.delegate, name = name)
            }
        }
        taskJobs.compute(id) { _, job ->
            job?.takeIf { it.isActive } ?: listen(id)
        }
    }

    public fun remove(id: K, subject: Contact): Unit = synchronized(tasks) {
        tasks.compute(id) { _, info ->
            info?.run {
                copy(contacts = contacts - subject.delegate)
            }
        }
        if (infos(id).isEmpty()) {
            tasks.remove(id)
            taskJobs.remove(id)?.cancel()
        }
    }

    public fun detail(subject: Contact): String = buildString {
        appendLine("# 订阅列表")
        appendLine("| NAME | ID | LAST | ACTIVE |")
        appendLine("|------|----|------|--------|")
        for ((id, info) in tasks) {
            if (subject.delegate !in info.contacts) continue
            appendLine("| ${info.name} | $id | ${info.last} | ${taskJobs[id]?.isActive} |")
        }
    }
}