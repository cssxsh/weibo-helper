package xyz.cssxsh.weibo.api

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonObject
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.data.*
import java.time.YearMonth

suspend fun WeiboClient.getUserMicroBlogs(
    uid: Long,
    page: Int = 1,
    feature: Int = 0,
    display: Int = 0,
    retcode: Int = 6102,
): UserBlog = temp(STATUSES_MY_MICRO_BLOG) {
    header(HttpHeaders.Referrer, "https://www.weibo.com/u/${uid}")

    parameter("uid", uid)
    parameter("page", page)
    parameter("feature", feature)
    parameter("display", display)
    parameter("retcode", retcode)
}

suspend fun WeiboClient.getMicroBlog(
    mid: Long
) = getMicroBlog(mid.toString())

suspend fun WeiboClient.getMicroBlog(
    mid: String
): MicroBlog = get(STATUSES_SHOW) {
    header(HttpHeaders.Referrer, "https://www.weibo.com/detail/${mid}")

    parameter("id", mid)
}

suspend fun WeiboClient.getLongText(
    mid: Long
) = getLongText(mid.toString())

suspend fun WeiboClient.getLongText(
    mid: String
): LongTextContent = temp(STATUSES_LONGTEXT) {
    header(HttpHeaders.Referrer, "https://www.weibo.com/detail/${mid}")

    parameter("id", mid)
}

suspend fun WeiboClient.getMentions(
    author: Boolean = false,
    type: Boolean = false
): UserMention = temp(STATUSES_MENTIONS) {
    header(HttpHeaders.Referrer, "https://weibo.com/at/weibo")

    parameter("filter_by_author", author.toInt())
    parameter("filter_by_type", type.toInt())
}