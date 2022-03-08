package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import kotlinx.serialization.*
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.weibo.data.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.api.*
import java.net.*
import java.time.*

abstract class WeiboSubscriber<K : Comparable<K>>(val type: String) :
    CoroutineScope by WeiboHelperPlugin.childScope("WeiboListener-$type") {

    companion object {
        private val all = mutableListOf<WeiboSubscriber<*>>()

        fun start() {
            for (subscriber in all) {
                subscriber.start()
            }
        }

        fun stop() {
            for (subscriber in all) {
                subscriber.stop()
            }
        }
    }

    init {
        let(all::add)
    }

    abstract val load: suspend (id: K) -> List<MicroBlog>

    protected open val filter: WeiboFilter get() = WeiboHelperSettings

    private val forward get() = UseForwardMessage

    protected abstract val tasks: MutableMap<K, WeiboTaskInfo>

    private val taskJobs = mutableMapOf<K, Job>()

    private fun infos(id: K) = tasks[id]?.contacts.orEmpty()

    fun start(): Unit = synchronized(taskJobs) {
        for ((id, _) in tasks) {
            taskJobs[id] = listen(id)
        }
    }

    fun stop(): Unit = synchronized(taskJobs) {
        coroutineContext.cancelChildren()
        taskJobs.clear()
    }

    private suspend fun sendMessageToTaskContacts(id: K, build: BuildMessage) {
        for (delegate in infos(id)) {
            try {
                requireNotNull(findContact(delegate)) { "找不到用户" }.let { contact ->
                    contact.sendMessage(build(contact))
                }
            } catch (e: Throwable) {
                logger.warning({ "对[${delegate}]构建消息失败" }, e)
            }
        }
    }

    private operator fun LocalTime.minus(other: LocalTime): Duration =
        Duration.ofSeconds((toSecondOfDay() - other.toSecondOfDay()).toLong())

    private fun Map<Long, MicroBlog>.near(time: LocalTime = LocalTime.now()): Boolean {
        return values.map { it.created.toLocalTime() - time }.any { it.abs() < IntervalSlow }
    }

    protected open val reposts = true

    protected open val predicate: (MicroBlog, K, MutableSet<Long>) -> Boolean = filter@{ blog, id, histories ->
        val source = blog.retweeted ?: blog
        if (reposts && source.reposts < filter.repost) {
            logger.verbose { "${type}(${id}) 转发数屏蔽，跳过 ${source.id} ${source.reposts}" }
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
            logger.verbose { "${type}(${id}) Url屏蔽，跳过 ${source.id} ${blog.urls}" }
            return@filter false
        }
        if (histories.add(source.id).not()) {
            logger.verbose { "${type}(${id}) 历史屏蔽，跳过 ${source.id} ${source.created}" }
            return@filter false
        }
        true
    }

    private fun listen(id: K): Job = launch(SupervisorJob()) {
        logger.info { "添加对$type(${tasks.getValue(id).name}#${id})的监听任务" }
        val history by WeiboHistoryDelegate(id, this@WeiboSubscriber)
        val cache: MutableSet<Long> = HashSet()
        for ((_, blog) in history) {
            cache.add(blog.id)
            cache.add(blog.retweeted?.id ?: continue)
        }

        while (isActive && infos(id).isNotEmpty()) {
            delay((if (history.near()) IntervalFast else IntervalSlow).toMillis())
            try {
                val list = load(id).filter { predicate(it, id, cache) }
                if (list.isEmpty()) continue

                if (forward) {
                    sendMessageToTaskContacts(id) { contact ->
                        buildForwardMessage(contact) {
                            displayStrategy = object : ForwardMessage.DisplayStrategy {
                                override fun generateTitle(forward: RawForwardMessage): String =
                                    "${type}-${id}有新微博"

                                override fun generateSummary(forward: RawForwardMessage): String =
                                    "查看${list.size}条微博转发"
                            }
                            for (blog in list) {
                                contact.bot says blog.toMessage(contact)
                            }
                        }
                    }
                } else {
                    for (blog in list) {
                        if (blog.created > tasks.getValue(id).last) {
                            sendMessageToTaskContacts(id) { contact ->
                                blog.toMessage(contact)
                            }
                        }
                    }
                }

                for (blog in list) {
                    cache.add(blog.id)
                    cache.add(blog.retweeted?.id ?: continue)
                    history[blog.id] = blog
                }

                list.maxByOrNull { it.created }?.let { blog ->
                    logger.verbose { "$type(${id})[${blog.username}]最新微博时间为<${blog.created}>" }
                    tasks.compute(id) { _, info ->
                        info?.copy(last = blog.created)
                    }
                }
            } catch (exception: SerializationException) {
                logger.warning({ "$type(${id})监听任务序列化时失败" }, exception)
                sendLoginMessage("$type(${id})监听任务序列化时失败, $exception")
                continue
            } catch (exception: UnknownHostException) {
                //
            } catch (exception: Throwable) {
                logger.warning({ "WEIBO登陆状态将刷新" }, exception)
                try {
                    client.restore()
                } catch (_: Throwable) {
                    //
                }
            } finally {
                logger.info { "$type(${id}): ${tasks[id]}监听任务完成一次, 即将进入延时" }
            }
        }
    }

    fun add(id: K, name: String, subject: Contact): Unit = synchronized(tasks) {
        tasks.compute(id) { _, info ->
            with(info ?: WeiboTaskInfo(name = name)) {
                copy(contacts = contacts + subject.delegate)
            }
        }
        taskJobs.compute(id) { _, job ->
            job?.takeIf { it.isActive } ?: listen(id)
        }
    }

    fun remove(id: K, subject: Contact): Unit = synchronized(tasks) {
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

    fun detail(subject: Contact): String = buildString {
        appendLine("# 订阅列表")
        appendLine("| NAME | ID | LAST | ACTIVE |")
        appendLine("|------|----|------|--------|")
        for ((id, info) in tasks) {
            if (subject.delegate !in info.contacts) continue
            appendLine("| ${info.name} | $id | ${info.last} | ${taskJobs[id]?.isActive} |")
        }
    }
}