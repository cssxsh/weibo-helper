package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.plugin.command.*
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.api.*
import kotlin.time.*

object WeiboHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.weibo-helper", "1.0.0-dev-2") {
        name("weibo-helper")
        author("cssxsh")
    }
) {

    override val autoSaveIntervalMillis: LongRange
        get() = (3).minutes.toLongMilliseconds()..(10).minutes.toLongMilliseconds()

    internal val client by lazy { WeiboClient(WeiboStatusData.status) }

    private lateinit var clear: Job

    override fun onEnable() {
        WeiboTaskData.reload()
        WeiboHelperSettings.reload()
        WeiboStatusData.reload()

        runBlocking {
            runCatching {
                client.flush()
            }.onSuccess {
                logger.info { "登陆成功, $it" }
            }.onFailure {
                logger.warning { "登陆失败, ${it.message}, 请尝试使用 /wlogin 指令登录" }
            }
        }

        WeiboSubscriber.start()

        WeiboUserCommand.listener.start()
        WeiboGroupCommand.listener.start()

        WeiboUserCommand.register()
        WeiboGroupCommand.register()
        WeiboCacheCommand.register()
        WeiboLoginCommand.register()

        clear = clear()
    }

    override fun onDisable() {
        WeiboUserCommand.unregister()
        WeiboGroupCommand.unregister()
        WeiboCacheCommand.unregister()
        WeiboLoginCommand.unregister()

        WeiboUserCommand.listener.stop()
        WeiboGroupCommand.listener.stop()

        WeiboSubscriber.stop()

        WeiboStatusData.status = client.status()

        clear.cancel()
    }
}