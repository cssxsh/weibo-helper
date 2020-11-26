package xyz.cssxsh.weibo.api

import io.ktor.client.request.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import xyz.cssxsh.weibo.WeiboClient
import xyz.cssxsh.weibo.data.CardData
import xyz.cssxsh.weibo.data.UserData
import xyz.cssxsh.weibo.data.blog.*

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

fun CardData.getBlogs(
    ignore: (JsonObject, Throwable) -> Boolean = { _, _ -> true }
): List<Blog> = data.cards.mapNotNull { it.jsonObject["mblog"]?.jsonObject }.map { jsonObject ->
    runCatching {
        when(jsonObject.getValue("mblogtype").jsonPrimitive.content) {
            "0" -> Json.decodeFromJsonElement(TextBlog.serializer(), jsonObject)
            "1" -> Json.decodeFromJsonElement(PicBlog.serializer(), jsonObject)
            "2" -> Json.decodeFromJsonElement(VideoBlog.serializer(), jsonObject)
            else -> throw IllegalArgumentException("未知类型, json: $jsonObject")
        }
    }.onFailure {
        if (ignore(jsonObject, it).not()) throw it
    }.getOrElse {
        Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            isLenient = true
            allowStructuredMapKeys = true
        }.decodeFromJsonElement(TempBlog.serializer(), jsonObject)
    }
}