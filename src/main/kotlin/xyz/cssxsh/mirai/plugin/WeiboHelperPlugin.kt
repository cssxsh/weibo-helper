package xyz.cssxsh.mirai.plugin

import com.google.auto.service.AutoService
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.utils.hoursToMillis
import net.mamoe.mirai.utils.minutesToMillis
import xyz.cssxsh.mirai.plugin.command.WeiboCommand
import xyz.cssxsh.mirai.plugin.data.WeiboTaskData

@AutoService(JvmPlugin::class)
object WeiboHelperPlugin : KotlinPlugin(
    JvmPluginDescription("xyz.cssxsh.mirai.plugin.weibo-helper", "0.1.0-dev-1") {
        name("weibo-helper")
        author("cssxsh")
    }
) {

    @ConsoleExperimentalApi
    override val autoSaveIntervalMillis: LongRange
        get() = 3.minutesToMillis..30.hoursToMillis

    override fun onEnable() {
        WeiboTaskData.reload()
        WeiboCommand.onInit()
        WeiboCommand.register()
    }

    override fun onDisable() {
        WeiboCommand.unregister()
    }
}