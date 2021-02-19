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
): MicroBlogData = useHttpClient { client ->
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