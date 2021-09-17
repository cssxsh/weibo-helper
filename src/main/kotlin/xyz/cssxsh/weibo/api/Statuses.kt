package xyz.cssxsh.weibo.api

import io.ktor.client.request.*
import io.ktor.http.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.data.*
import java.time.*

enum class FeatureType(private val value: Int) {
    ALL(0),
    ORIGINAL(1),
    HOT(2),
    ARTICLE(10);

    override fun toString(): String = value.toString()
}

enum class ChannelType(val id: Int) {
    ALL(1),
    USER(3),
    NOW(61),
    FOLLOW(62),
    VIDEO(64),
    IMAGE(63),
    ARTICLE(21),
    HOT(60),
    TOPIC(32)
    ;
}

suspend fun WeiboClient.getEmoticon(): EmotionData = temp(STATUSES_CONFIG) {
    header(HttpHeaders.Referrer, "https://www.weibo.com/home")
}

suspend fun WeiboClient.getUserMicroBlogs(
    uid: Long,
    page: Int,
    feature: FeatureType = FeatureType.ALL,
    month: YearMonth? = null
): UserBlog = temp(STATUSES_MY_MICRO_BLOG) {
    header(HttpHeaders.Referrer, "https://www.weibo.com/u/${uid}")

    parameter("uid", uid)
    parameter("page", page)
    parameter("feature", feature)
    parameter("stat_date", month?.run { "%04d%02d".format(year, monthValue) })
}

suspend fun WeiboClient.getUserHot(uid: Long, page: Int): UserBlog = temp(PROFILE_MY_HOT) {
    header(HttpHeaders.Referrer, "https://www.weibo.com/u/${uid}")

    parameter("uid", uid)
    parameter("page", page)
    parameter("feature", FeatureType.HOT)
}

suspend fun WeiboClient.getMicroBlog(id: Long) = getMicroBlog(id.toString())

suspend fun WeiboClient.getMicroBlog(mid: String): MicroBlog = json(STATUSES_SHOW) {
    header(HttpHeaders.Referrer, "https://www.weibo.com/detail/${mid}")

    parameter("id", mid)
}

suspend fun WeiboClient.getLongText(id: Long) = getLongText(id.toString())

suspend fun WeiboClient.getLongText(mid: String): LongTextContent = temp(STATUSES_LONGTEXT) {
    header(HttpHeaders.Referrer, "https://www.weibo.com/detail/${mid}")

    parameter("id", mid)
}

suspend fun WeiboClient.getMentions(author: Boolean = false, type: Boolean = false): UserMention {
    return temp(STATUSES_MENTIONS) {
        header(HttpHeaders.Referrer, "https://weibo.com/at/weibo")

        parameter("filter_by_author", author.toInt())
        parameter("filter_by_type", type.toInt())
    }
}

suspend fun WeiboClient.search(
    keyword: String,
    type: ChannelType = ChannelType.ALL,
    page: Int = 1,
    count: Int = PAGE_SIZE
): SearchResult = temp(SEARCH_ALL) {
    header(HttpHeaders.Referrer, "https://weibo.com/search")

    parameter("containerid", "100103type=${type.id}&q=${keyword}&t=1")
    parameter("page", page)
    parameter("count", count)
}