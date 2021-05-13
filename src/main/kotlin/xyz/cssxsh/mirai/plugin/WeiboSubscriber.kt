package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.plugin.WeiboHelperPlugin.client
import xyz.cssxsh.mirai.plugin.WeiboHelperPlugin.logger
import xyz.cssxsh.weibo.api.*

object WeiboSubscriber {

    private lateinit var job: Job

    /**
     * 1. https://m.weibo.cn/status/JFzsgd0CX
     * 2. https://m.weibo.cn/status/4585001998353993
     * 3. https://weibo.com/5594511989/JzFhZz3fP
     * 4. https://weibo.com/detail/JzFhZz3fP
     * 5. https://weibo.com/detail/4585001998353993
     */
    private val WEIBO_REGEX = """(?<=(m\.weibo\.cn/status/|(www\.)?weibo\.com/(\d{1,32}|detail)/))[0-9A-z]+""".toRegex()

    fun start() = GlobalEventChannel.parentScope(WeiboHelperPlugin).subscribeMessages {
        WEIBO_REGEX findingReply { result ->
            logger.info { "[${sender}] 匹配WEIBO(${result.value})" }
            runCatching {
                message.quote() + client.getMicroBlog(mid = result.value).buildMessage(contact = subject)
            }.onFailure {
                logger.warning({ "构建DYNAMIC(${result.value})信息失败，尝试重新登陆" }, it)
                runCatching {

                    client.login()
                }.onSuccess {
                    logger.info { "登录成功, $it" }
                }
            }.getOrElse {
                it.message
            }
        }
    }.also { job = it }

    fun stop() = job.cancel()
}