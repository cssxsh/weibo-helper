package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value
import xyz.cssxsh.weibo.data.LoginStatus

object WeiboStatusData : AutoSavePluginData("WeiboStatusData") {

    @ValueDescription("登录状态")
    var status by value(LoginStatus())
}