package xyz.cssxsh.mirai.weibo

import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.extension.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.weibo.command.*
import xyz.cssxsh.mirai.weibo.data.*

object WeiboHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.weibo-helper", "1.5.2") {
        name("weibo-helper")
        author("cssxsh")
    }
) {
    private var clear: Job? = null

    private var restore: Job? = null

    override fun PluginComponentStorage.onLoad() {
        runAfterStartup {
            launch {
                client.init()

                clear = this@WeiboHelperPlugin.launch(Dispatchers.IO) {
                    clear()
                }
                restore = this@WeiboHelperPlugin.launch(Dispatchers.IO) {
                    restore()
                }

                WeiboListener.start()
                WeiboSubscriber.start()
            }
        }
    }

    override fun onEnable() {
        WeiboTaskData.reload()
        WeiboHelperSettings.reload()
        WeiboHelperSettings.save()
        WeiboStatusData.reload()
        WeiboEmoticonData.reload()

        for (command in WeiboHelperCommand) {
            command.register()
        }

        logger.info { "图片缓存位置 ${ImageCache.absolutePath}" }
    }

    override fun onDisable() {
        for (command in WeiboHelperCommand) {
            command.unregister()
        }

        WeiboSubscriber.stop()

        WeiboListener.stop()

        clear?.cancel()

        restore?.cancel()
    }
}