package xyz.cssxsh.mirai.weibo

import kotlinx.coroutines.*
import net.mamoe.mirai.console.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.extension.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.console.plugin.*
import net.mamoe.mirai.console.util.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.weibo.data.*
import xyz.cssxsh.weibo.*

@PublishedApi
internal object WeiboHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.weibo-helper", "1.6.2") {
        name("weibo-helper")
        author("cssxsh")
    }
) {
    private var clear: Job? = null

    private var restore: Job? = null

    override fun PluginComponentStorage.onLoad() {
        System.setProperty(SERIALIZATION_EXCEPTION_SAVE, dataFolderPath.toString())
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
    private inline fun <reified T : Any> spi(): Lazy<List<T>> = lazy {
        with(net.mamoe.mirai.console.internal.util.PluginServiceHelper) {
            jvmPluginClasspath.pluginClassLoader
                .findServices<T>()
                .loadAllServices()
        }
    }

    private val commands: List<Command> by spi()
    private val data: List<PluginData> by spi()
    private val config: List<PluginConfig> by spi()

    override fun onEnable() {
        // XXX: mirai console version check
        check(SemVersion.parseRangeRequirement(">= 2.12.0-RC").test(MiraiConsole.version)) {
            "$name $version 需要 Mirai-Console 版本 >= 2.12.0，目前版本是 ${MiraiConsole.version}"
        }

        for (command in commands) command.register()
        for (data in data) data.reload()
        for (config in config) config.reload()

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