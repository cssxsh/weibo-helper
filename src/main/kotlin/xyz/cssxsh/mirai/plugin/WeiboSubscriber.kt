package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.console.util.CoroutineScopeUtils.childScope
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.utils.*
import xyz.cssxsh.weibo.api.*

@ConsoleExperimentalApi
internal object WeiboSubscriber: CoroutineScope by WeiboHelperPlugin.childScope("WeiboSubscriber") {

    /**
     * 1. https://m.weibo.cn/status/JFzsgd0CX
     * 2. https://m.weibo.cn/status/4585001998353993
     * 3. https://weibo.com/5594511989/JzFhZz3fP
     * 4. https://weibo.com/detail/JzFhZz3fP
     * 5. https://weibo.com/detail/4585001998353993
     */
    private val WEIBO_REGEX = """(?<=(m\.weibo\.cn/status/|(www\.)?weibo\.com/(\d{1,32}|detail)/))[0-9A-z]+""".toRegex()

    fun start() {
        globalEventChannel().subscribeMessages {
            WEIBO_REGEX findingReply replier@{ result ->
                if (subject is Group && subject.id in QuietGroups) return@replier null

                logger.info { "[${sender}] 匹配WEIBO(${result.value})" }
                runCatching {
                    message.quote() + client.getMicroBlog(mid = result.value).buildMessage(contact = subject)
                }.onFailure {
                    logger.warning({ "构建WEIBO(${result.value})信息失败，尝试重新登陆" }, it)
                    runCatching {
                        client.flush()
                    }.onSuccess {
                        logger.info { "登录成功, $it" }
                    }
                }.getOrElse {
                    it.message
                }
            }
        }
    }

    fun stop() {
        coroutineContext.cancelChildren()
    }
}