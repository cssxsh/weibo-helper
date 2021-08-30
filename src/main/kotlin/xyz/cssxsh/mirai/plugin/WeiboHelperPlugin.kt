package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.console.util.*
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.plugin.command.*
import xyz.cssxsh.mirai.plugin.data.*

object WeiboHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.weibo-helper", "1.2.2") {
        name("weibo-helper")
        author("cssxsh")
    }
) {
    private lateinit var clear: Job

    @OptIn(ConsoleExperimentalApi::class)
    private fun <T : PluginConfig> T.save() = loader.configStorage.store(this@WeiboHelperPlugin, this)

    override fun onEnable() {
        WeiboTaskData.reload()
        WeiboHelperSettings.reload()
        WeiboHelperSettings.save()
        WeiboStatusData.reload()
        WeiboEmoticonData.reload()

        WeiboUserCommand.register()
        WeiboGroupCommand.register()
        WeiboCacheCommand.register()
        WeiboLoginCommand.register()
        WeiboDetailCommand.register()
        WeiboHotCommand.register()

        if (WeiboHelperSettings.quiet.isNotEmpty()) {
            logger.warning { "关闭链接监听的群号, 作废，请通过权限系统设置 /perm add g12345 xyz.cssxsh.mirai.plugin.weibo-helper:quiet.group" }
        }

        runBlocking {
            client.init()
        }

        WeiboListener.start()

        globalEventChannel().subscribeOnce<BotOnlineEvent> {
            WeiboUserCommand.subscriber.start()
            WeiboGroupCommand.subscriber.start()
            WeiboHotCommand.subscriber.start()
        }

        clear = launch {
            clear()
        }
    }

    override fun onDisable() {
        WeiboUserCommand.unregister()
        WeiboGroupCommand.unregister()
        WeiboCacheCommand.unregister()
        WeiboLoginCommand.unregister()
        WeiboDetailCommand.unregister()
        WeiboHotCommand.unregister()

        WeiboUserCommand.subscriber.stop()
        WeiboGroupCommand.subscriber.stop()
        WeiboHotCommand.subscriber.stop()

        WeiboListener.stop()

        clear.cancel()
    }
}