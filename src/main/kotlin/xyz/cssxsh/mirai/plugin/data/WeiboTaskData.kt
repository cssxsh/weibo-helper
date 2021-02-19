package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value

object WeiboTaskData : AutoSavePluginConfig("WeiboTaskData") {

    @ValueName("users")
    val users: MutableMap<Long, WeiboTaskInfo> by value(mutableMapOf())

    @ValueName("groups")
    val groups: MutableMap<Long, WeiboTaskInfo> by value(mutableMapOf())
}