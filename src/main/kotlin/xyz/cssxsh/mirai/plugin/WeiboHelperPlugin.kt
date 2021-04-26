package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.plugin.command.*
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.api.*
import kotlin.time.*

object WeiboHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.weibo-helper", "0.1.0-dev-1") {
        name("weibo-helper")
        author("cssxsh")
    }
) {

    @ConsoleExperimentalApi
    override val autoSaveIntervalMillis: LongRange
        get() = (3).minutes.toLongMilliseconds()..(10).minutes.toLongMilliseconds()

    internal lateinit var client : WeiboClient
        private set

    override fun onEnable() {
        WeiboTaskData.reload()
        WeiboHelperSettings.reload()

        client = WeiboClient(WeiboHelperSettings.initCookies)
        runBlocking {
            runCatching {
                client.login()
            }.onSuccess {
                logger.info { "登陆成功, $it" }
            }.onFailure {
                logger.warning({ "登陆失败" }, it)
            }
        }

        WeiboSubscriber.start()

        WeiboUserCommand.listener.start()
        WeiboGroupCommand.listener.start()

        WeiboUserCommand.register()
        WeiboGroupCommand.register()
    }

    override fun onDisable() {
        WeiboUserCommand.unregister()
        WeiboGroupCommand.unregister()

        WeiboUserCommand.listener.stop()
        WeiboGroupCommand.listener.stop()

        WeiboSubscriber.stop()
    }
}