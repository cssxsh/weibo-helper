package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.console.permission.PermissionService.Companion.testPermission
import net.mamoe.mirai.console.permission.PermitteeId.Companion.permitteeId
import net.mamoe.mirai.console.util.*
import net.mamoe.mirai.console.util.CoroutineScopeUtils.childScope
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.utils.*
import xyz.cssxsh.weibo.api.*

@OptIn(ConsoleExperimentalApi::class)
internal object WeiboListener
    : CoroutineScope by WeiboHelperPlugin.childScope("WeiboSubscriber") {

    /**
     * 1. https://m.weibo.cn/status/JFzsgd0CX
     * 2. https://m.weibo.cn/status/4585001998353993
     * 3. https://weibo.com/5594511989/JzFhZz3fP
     * 4. https://weibo.com/detail/JzFhZz3fP
     * 5. https://weibo.com/detail/4585001998353993
     * 6. https://m.weibo.cn/detail/4585001998353993
     */
    private val WEIBO_REGEX = """(?<=(weibo\.(cn|com)/(\d{1,32}|detail|status)/))[0-9A-z]+""".toRegex()

    private val QuietGroup = WeiboHelperPlugin.registerPermission("quiet.group", "关闭链接监听")

    fun start() {
        globalEventChannel().subscribeMessages {
            WEIBO_REGEX findingReply replier@{ result ->
                if (subject is Group || QuietGroup.testPermission((subject as Group).permitteeId)) return@replier null

                logger.info { "[${sender}] 匹配WEIBO(${result.value})" }
                runCatching {
                    message.quote() + client.getMicroBlog(mid = result.value).toMessage(contact = subject)
                }.onFailure {
                    logger.warning({ "构建WEIBO(${result.value})信息失败，尝试重新刷新" }, it)
                    runCatching {
                        client.restore()
                    }.onSuccess {
                        logger.info { "登录成功, $it" }
                    }.onFailure { cause ->
                        if ("login" in cause.message.orEmpty()) {
                            LoginContact?.sendMessage("WEIBO登陆状态失效，需要重新登陆 /wlogin ")
                        }
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