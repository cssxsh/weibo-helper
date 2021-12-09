package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import kotlinx.serialization.SerializationException
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.console.util.CoroutineScopeUtils.childScope
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.ForwardMessage
import net.mamoe.mirai.message.data.RawForwardMessage
import net.mamoe.mirai.message.data.buildForwardMessage
import net.mamoe.mirai.utils.info
import net.mamoe.mirai.utils.verbose
import net.mamoe.mirai.utils.warning
import xyz.cssxsh.mirai.plugin.data.WeiboTaskInfo
import xyz.cssxsh.weibo.api.restore
import xyz.cssxsh.weibo.data.MicroBlog
import xyz.cssxsh.weibo.uid
import xyz.cssxsh.weibo.username
import java.time.Duration
import java.time.LocalTime
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.any
import kotlin.collections.associateBy
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.contains
import kotlin.collections.filter
import kotlin.collections.flatMap
import kotlin.collections.getValue
import kotlin.collections.isNotEmpty
import kotlin.collections.iterator
import kotlin.collections.listOf
import kotlin.collections.map
import kotlin.collections.maxByOrNull
import kotlin.collections.minus
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.orEmpty
import kotlin.collections.plus
import kotlin.collections.set
import kotlin.collections.toMutableSet

@OptIn(ConsoleExperimentalApi::class)
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

    protected open val filter: WeiboFilter get() = WeiboRecordFilter

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
        var history by WeiboHistoryDelegate(id, this@WeiboSubscriber)
        while (isActive && infos(id).isNotEmpty()) {
            delay((if (history.near()) IntervalFast else IntervalSlow).toMillis())
            try {
                val histories = history.values.flatMap { listOf(it.id, it.retweeted?.id ?: 0) }.toMutableSet()
                val list = load(id).filter { predicate(it, id, histories) }

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

                history = history + list.associateBy { it.id }

                list.maxByOrNull { it.created }?.let { blog ->
                    logger.verbose { "$type(${id})[${blog.username}]最新微博时间为<${blog.created}>" }
                    tasks.compute(id) { _, info ->
                        info?.copy(last = blog.created)
                    }
                }
            } catch (exception: SerializationException) {
                logger.warning({ "$type(${id})监听任务序列化时失败" }, exception)
                try {
                    sendLoginMessage("$type(${id})监听任务序列化时失败, $exception")
                } catch (_: Throwable) {
                    //
                }
                continue
            } catch (exception: Throwable) {
                try {
                    client.restore()
                } catch (cause: Throwable) {
                    logger.warning({ "WEIBO登陆状态失效，需要重新登陆" }, cause)
                    try {
                        sendLoginMessage("WEIBO登陆状态失效，需要重新登陆 /wlogin $cause")
                    } catch (_: Throwable) {
                        //
                    }
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