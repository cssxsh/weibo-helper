package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.*
import xyz.cssxsh.weibo.data.*

object WeiboStatusData : AutoSavePluginData("WeiboStatusData") {

    @ValueDescription("登录状态")
    var status by value(LoginStatus())
}