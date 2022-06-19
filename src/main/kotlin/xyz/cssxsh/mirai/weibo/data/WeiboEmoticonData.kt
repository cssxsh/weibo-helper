package xyz.cssxsh.mirai.weibo.data

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import net.mamoe.mirai.console.data.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.data.*

object WeiboEmoticonData : AutoSavePluginData("WeiboEmoticonData") {

    @OptIn(ExperimentalSerializationApi::class)
    internal fun default() = this::class.java.getResourceAsStream("Emoticons.json").use {
        WeiboClient.Json.decodeFromStream<List<Emoticon>>(requireNotNull(it) { "找不到Emoticons初始化文件" })
    }

    @ValueDescription("表情数据")
    val emoticons: MutableMap<String, Emoticon> by value {
        if (isEmpty()) {
            for (item in default()) {
                put(item.phrase, item)
            }
        }
    }
}