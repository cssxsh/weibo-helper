package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value

object WeiboTaskData : AutoSavePluginConfig("WeiboTaskData") {

    @ValueName("tasks")
    val tasks: MutableMap<Long, TaskInfo> by value(mutableMapOf())
}