package xyz.cssxsh.weibo.api

import io.ktor.client.request.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import xyz.cssxsh.weibo.WeiboClient
import xyz.cssxsh.weibo.data.CardData
import xyz.cssxsh.weibo.data.BlogCard
import xyz.cssxsh.weibo.data.UserData
import xyz.cssxsh.weibo.data.blog.Blog
import xyz.cssxsh.weibo.data.blog.TextBlog
import xyz.cssxsh.weibo.data.blog.VideoBlog

suspend fun WeiboClient.userData(
    uid: Long,
): UserData = useHttpClient { client ->
    client.get(WeiboApi.M_GET_INDEX) {
        parameter("type", "uid")
        parameter("value", uid)
        parameter("containerid", WeiboApi.USER_DATA_ID + uid)
    }
}

suspend fun WeiboClient.cardData(
    uid: Long,
): CardData = useHttpClient { client ->
    client.get(WeiboApi.M_GET_INDEX) {
        parameter("type", "uid")
        parameter("value", uid)
        parameter("containerid", WeiboApi.CARD_DATA_ID + uid)
    }
}

fun CardData.getBlogs(): List<Blog> = data.cards.mapNotNull {
    it.jsonObject["mblog"]?.jsonObject
}.map {
    when(it["mblogtype"]?.jsonPrimitive?.content) {
        "0" -> Json.decodeFromJsonElement(TextBlog.serializer(), it)
        "2" -> Json.decodeFromJsonElement(VideoBlog.serializer(), it)
        else -> throw IllegalArgumentException("未知类型, json: $it")
    }
}