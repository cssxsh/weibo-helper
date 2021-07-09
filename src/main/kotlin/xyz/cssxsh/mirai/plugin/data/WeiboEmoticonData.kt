package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value
import xyz.cssxsh.weibo.data.Emoticon

object WeiboEmoticonData : AutoSavePluginData("WeiboEmoticonData") {
    val emoticons by value(mutableMapOf<String, Emoticon>())
}