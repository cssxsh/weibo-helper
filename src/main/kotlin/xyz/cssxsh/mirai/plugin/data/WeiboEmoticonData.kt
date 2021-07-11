package xyz.cssxsh.mirai.plugin.data

import kotlinx.serialization.decodeFromString
import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value
import xyz.cssxsh.weibo.WeiboClient
import xyz.cssxsh.weibo.data.Emoticon

object WeiboEmoticonData : AutoSavePluginData("WeiboEmoticonData") {

    internal fun default() = this::class.java.getResourceAsStream("Emoticons.json").use {
        val text = requireNotNull(it) { "找不到Emoticons初始化文件" }.reader().readText()
        WeiboClient.Json.decodeFromString<List<Emoticon>>(text)
    }

    @ValueDescription("表情数据")
    val emoticons: MutableMap<String, Emoticon> by value {
        if (isEmpty()) {
            default().forEach { put(it.phrase, it) }
        }
    }
}