package xyz.cssxsh.mirai.weibo.data

import net.mamoe.mirai.console.data.*
import xyz.cssxsh.weibo.data.*

@PublishedApi
internal object WeiboStatusData : AutoSavePluginData("WeiboStatusData") {

    @ValueDescription("登录状态")
    var status by value(LoginStatus())
}