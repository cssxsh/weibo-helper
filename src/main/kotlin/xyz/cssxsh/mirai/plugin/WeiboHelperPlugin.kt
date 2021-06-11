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

object WeiboHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.weibo-helper", "1.0.0-dev-2") {
        name("weibo-helper")
        author("cssxsh")
    }
) {

    internal val client by lazy { WeiboClient(WeiboStatusData.status) }

    private lateinit var clear: Job

    override fun onEnable() {
        WeiboTaskData.reload()
        WeiboHelperSettings.reload()
        WeiboStatusData.reload()

        runBlocking {
            runCatching {
                client.restore()
            }.onSuccess {
                logger.info { "登陆成功, $it" }
            }.onFailure {
                logger.warning { "登陆失败, ${it.message}, 请尝试使用 /wlogin 指令登录" }
            }.recoverCatching {
                client.incarnate()
            }.onFailure {
                logger.warning { "模拟游客失败, ${it.message}" }
            }
        }

        WeiboSubscriber.start()

        WeiboUserCommand.listener.start()
        WeiboGroupCommand.listener.start()

        WeiboUserCommand.register()
        WeiboGroupCommand.register()
        WeiboCacheCommand.register()
        WeiboLoginCommand.register()
        WeiboDetailCommand.register()

        clear = clear()
    }

    override fun onDisable() {
        WeiboUserCommand.unregister()
        WeiboGroupCommand.unregister()
        WeiboCacheCommand.unregister()
        WeiboLoginCommand.unregister()
        WeiboDetailCommand.unregister()

        WeiboUserCommand.listener.stop()
        WeiboGroupCommand.listener.stop()

        WeiboSubscriber.stop()

        WeiboStatusData.status = client.status()

        clear.cancel()
    }
}