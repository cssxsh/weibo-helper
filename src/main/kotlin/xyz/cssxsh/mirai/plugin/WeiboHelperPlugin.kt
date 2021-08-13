package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.console.util.*
import xyz.cssxsh.mirai.plugin.command.*
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.data.*

object WeiboHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.weibo-helper", "1.1.2") {
        name("weibo-helper")
        author("cssxsh")
    }
) {

    internal val client: WeiboClient by lazy {
        object : WeiboClient(ignore = ClientIgnore) {
            override var info: LoginUserInfo
                get() = super.info
                set(value) {
                    super.info = value
                    launch { WeiboStatusData.status = status() }
                }

            init {
                load(WeiboStatusData.status)
            }
        }
    }

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

        runBlocking {
            client.init()
        }

        WeiboListener.start()

        WeiboUserCommand.subscriber.start()
        WeiboGroupCommand.subscriber.start()
        WeiboHotCommand.subscriber.start()

        clear = clear()
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

        WeiboStatusData.status = client.status()

        clear.cancel()
    }
}