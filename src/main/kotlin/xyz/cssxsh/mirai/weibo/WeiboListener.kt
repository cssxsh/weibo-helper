package xyz.cssxsh.mirai.weibo

import kotlinx.coroutines.*
import kotlinx.serialization.*
import net.mamoe.mirai.console.permission.PermissionService.Companion.testPermission
import net.mamoe.mirai.console.permission.PermitteeId.Companion.permitteeId
import net.mamoe.mirai.console.util.ContactUtils.render
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.weibo.data.*
import xyz.cssxsh.weibo.api.*
import kotlin.coroutines.*

internal object WeiboListener : CoroutineScope {

    override val coroutineContext: CoroutineContext =
        CoroutineName(name = "WeiboSubscriber") + SupervisorJob() + CoroutineExceptionHandler { context, throwable ->
            logger.warning({ "$throwable in $context" }, throwable)
        }

    /**
     * * [https://m.weibo.cn/status/JFzsgd0CX]
     * * [https://m.weibo.cn/status/4585001998353993]
     * * [https://weibo.com/5594511989/JzFhZz3fP]
     * * [https://weibo.com/detail/JzFhZz3fP]
     * * [https://weibo.com/detail/4585001998353993]
     * * [https://m.weibo.cn/detail/4585001998353993]
     */
    private val WEIBO_REGEX = """(?<=(weibo\.(cn|com)/(\d{1,32}|detail|status)/))[0-9A-z]+""".toRegex()

    private val Parser = WeiboHelperPlugin.registerPermission("auto.parser", "自动解析微博链接")

    private val interval get() = WeiboHelperSettings.interval

    private val cache: MutableMap<Long, MutableMap<String, Long>> = HashMap()

    private fun cache(subject: Contact, match: MatchResult): Boolean {
        val history = cache.getOrPut(subject.id) { HashMap() }
        val current = System.currentTimeMillis()
        return current != history.merge(match.value, current) { old, new -> if (new - old > interval) new else old }
    }

    fun start() {
        globalEventChannel().subscribeMessages {
            WEIBO_REGEX findingReply replier@{ result ->
                if (subject is Group) {
                    val permitteeId = (sender as? NormalMember ?: return@replier null).permitteeId
                    if (Parser.testPermission(permitteeId).not()) return@replier null
                }

                if (cache(subject, result)) return@replier null

                logger.info { "${sender.render()} 匹配WEIBO(${result.value})" }
                try {
                    message.quote() + client.getMicroBlog(mid = result.value).toMessage(contact = subject)
                } catch (exception: SerializationException) {
                    logger.warning({ "构建WEIBO(${result.value})序列化时失败" }, exception)
                    sendLoginMessage("构建WEIBO(${result.value})任务序列化时失败")
                    exception.message
                } catch (cause: Exception) {
                    logger.warning({ "构建WEIBO(${result.value})信息失败，尝试重新刷新" }, cause)
                    try {
                        client.restore()
                        null
                    } catch (cause: Exception) {
                        logger.warning({ "WEIBO登陆状态失效，需要重新登陆" }, cause)
                        sendLoginMessage("WEIBO登陆状态失效，需要重新登陆 /wlogin")
                        cause.message
                    } ?: cause.message
                }
            }
        }
    }

    fun stop() {
        coroutineContext.cancelChildren()
    }
}