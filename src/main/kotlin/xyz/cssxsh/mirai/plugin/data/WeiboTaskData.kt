package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.*

object WeiboTaskData : AutoSavePluginData("WeiboTaskData") {

    @ValueDescription("微博用户订阅器，KEY是UID")
    val users: MutableMap<Long, WeiboTaskInfo> by value(mutableMapOf())

    @ValueDescription("微博分组订阅器，KEY是GID")
    val groups: MutableMap<Long, WeiboTaskInfo> by value(mutableMapOf())

    @ValueDescription("微博热搜订阅器，KEY是 KEYWORD")
    val hots: MutableMap<String, WeiboTaskInfo> by value(mutableMapOf())
}