package xyz.cssxsh.mirai.weibo.data

import kotlinx.serialization.*
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.util.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.data.*
import java.io.*

@PublishedApi
internal object WeiboEmoticonData : AutoSavePluginData("WeiboEmoticonData") {

    @PublishedApi
    internal fun default(): Map<String, Emoticon> {
        val url = this::class.java.getResource("Emoticons.json") ?: throw FileNotFoundException("Emoticons.json")
        return WeiboClient.Json.decodeFromString(url.readText())
    }

    @ConsoleExperimentalApi
    override fun shouldPerformAutoSaveWheneverChanged(): Boolean = false

    @ValueDescription("表情数据")
    val emoticons: MutableMap<String, Emoticon> by value { putAll(default()) }
}