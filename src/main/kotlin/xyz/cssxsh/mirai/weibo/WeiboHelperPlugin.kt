package xyz.cssxsh.mirai.weibo

import kotlinx.coroutines.*
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.extension.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.console.plugin.*
import net.mamoe.mirai.console.util.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.weibo.data.*

object WeiboHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.weibo-helper", "1.5.7") {
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

    @Suppress("INVISIBLE_MEMBER")
    private inline fun <reified T : Any> services(): Lazy<List<T>> = lazy {
        with(net.mamoe.mirai.console.internal.util.PluginServiceHelper) {
            jvmPluginClasspath.pluginClassLoader
                .findServices<T>()
                .loadAllServices()
        }
    }

    private val commands: List<Command> by services()

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

        for (command in commands) command.register()

        logger.info { "图片缓存位置 ${ImageCache.absolutePath}" }
    }

    override fun onDisable() {
        for (command in commands) command.unregister()

        WeiboSubscriber.stop()

        WeiboListener.stop()

        clear?.cancel()

        restore?.cancel()
    }
}