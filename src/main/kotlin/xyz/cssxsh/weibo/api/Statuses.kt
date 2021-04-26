package xyz.cssxsh.weibo.api

import io.ktor.client.request.*
import io.ktor.http.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.data.*

suspend fun WeiboClient.getUserMicroBlogs(
    uid: Long,
    page: Int = 1,
    feature: Int = 0,
    display: Int = 0,
    retcode: Int = 6102,
): UserBlogData = useHttpClient { client ->
    client.get(WeiboApi.STATUSES_MY_MICRO_BLOG) {
        header(HttpHeaders.Referrer, "https://www.weibo.com/u/${uid}")

        parameter("uid", uid)
        parameter("page", page)
        parameter("feature", feature)
        parameter("display", display)
        parameter("retcode", retcode)
    }
}

suspend fun WeiboClient.getMicroBlog(
    mid: Long
) = getMicroBlog(mid.toString())

suspend fun WeiboClient.getMicroBlog(
    mid: String
): SimpleMicroBlog = useHttpClient { client ->
    client.get(WeiboApi.STATUSES_SHOW) {
        header(HttpHeaders.Referrer, "https://www.weibo.com/detail/${mid}")

        parameter("id", mid)
    }
}

suspend fun WeiboClient.getLongText(
    mid: Long
) = getLongText(mid.toString())

suspend fun WeiboClient.getLongText(
    mid: String
): LongTextData = useHttpClient { client ->
    client.get(WeiboApi.STATUSES_LONGTEXT) {
        header(HttpHeaders.Referrer, "https://www.weibo.com/detail/${mid}")

        parameter("id", mid)
    }
}

suspend fun WeiboClient.getUserMentions(
    filterByAuthor: Boolean = false,
    filterByType: Boolean = false
): UserMentionData = useHttpClient { client ->
    client.get(WeiboApi.STATUSES_MENTIONS) {
        header(HttpHeaders.Referrer, "https://weibo.com/at/weibo")

        parameter("filter_by_author", if (filterByAuthor) 1 else 0)
        parameter("filter_by_type", if (filterByType) 1 else 0)
    }
}