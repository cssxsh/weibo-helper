package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.plugin.command.*
import xyz.cssxsh.mirai.plugin.data.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*

object WeiboHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.weibo-helper", "1.1.1") {
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

    private fun start() = launch {
        runCatching {
            client.restore()
        }.onSuccess {
            logger.info { "登陆成功, $it" }
        }.onFailure {
            logger.warning { "登陆失败, ${it.message}, 请尝试使用 /wlogin 指令登录" }
            runCatching {
                client.incarnate()
            }.onSuccess {
                logger.info { "模拟游客成功，置信度${it}" }
            }.onFailure {
                logger.warning { "模拟游客失败, ${it.message}" }
            }
        }

        runCatching {
            client.getEmoticon().emoticon.let { map ->
                (map.brand.values + map.usual + map.more).flatMap { it.values.flatten() }.associateBy {
                    it.phrase
                }.let {
                    Emoticons.putAll(it)
                }
            }
        }.onSuccess {
            logger.info { "加载表情成功" }
        }.onFailure {
            logger.warning { "加载表情失败, $it" }
        }
    }

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
            start()
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