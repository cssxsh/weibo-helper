package xyz.cssxsh.mirai.weibo

import kotlinx.coroutines.*
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.extension.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.console.plugin.*
import net.mamoe.mirai.console.util.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.weibo.command.*
import xyz.cssxsh.mirai.weibo.data.*

object WeiboHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.weibo-helper", "1.5.5") {
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
        // XXX: mirai console version check
        check(SemVersion.parseRangeRequirement(">= 2.12.0-RC").test(MiraiConsole.version)) {
            "$name $version 需要 Mirai-Console 版本 >= 2.12.0，目前版本是 ${MiraiConsole.version}"
        }

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