package xyz.cssxsh.weibo.api

import io.ktor.client.request.*
import io.ktor.http.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.data.*

suspend fun WeiboClient.getFeedGroups(
    isNewSegment: Boolean = true,
    fetchHot: Boolean = true
): UserGroupData = get(FEED_ALL_GROUPS) {
    header(HttpHeaders.Referrer, INDEX_PAGE)

    parameter("is_new_segment", if (isNewSegment) 1 else 0)
    parameter("fetch_hot", if (fetchHot) 1 else 0)
}

enum class TimelineType(val url: String) {
    UNREAD_FRIENDS(url = FEED_UNREAD_FRIENDS_TIMELINE),
    GROUPS(url = FEED_GROUPS_TIMELINE),
    FRIENDS(url = FEED_FRIENDS_TIMELINE);
}

internal suspend fun WeiboClient.getTimeline(
    gid: Long,
    sinceId: Long?,
    maxId: Long?,
    count: Int,
    refresh: Int,
    fastRefresh: Int?,
    url: String
): TimelineData = get(url) {
    header(HttpHeaders.Referrer, "https://weibo.com/mygroups?gid=$gid")

    parameter("list_id", gid)
    parameter("since_id", sinceId)
    parameter("max_id", maxId)
    parameter("refresh", refresh)
    parameter("fast_refresh", fastRefresh)
    parameter("count", count)
}

suspend fun WeiboClient.getTimeline(
    gid: Long,
    sinceId: Long? = null,
    maxId: Long? = null,
    count: Int = STATUSES_PAGE_SIZE,
    refresh: Int = 0,
    fastRefresh: Int? = null,
    type: TimelineType = TimelineType.GROUPS
) = getTimeline(
    gid = gid,
    maxId = maxId,
    sinceId = sinceId,
    count = count,
    refresh = refresh,
    fastRefresh = fastRefresh,
    url = type.url
)

suspend fun WeiboClient.getHot(
    gid: Long,
    maxId: Long? = null,
    extend: List<String> = listOf("discover", "new_feed"),
    count: Int = STATUSES_PAGE_SIZE,
    refresh: Int = 0,
): TimelineData = get(FEED_HOT_TIMELINE) {
    header(HttpHeaders.Referrer, "https://weibo.com/hot/list/$gid")

    parameter("group_id", gid)
    parameter("containerid", gid)
    parameter("max_id", maxId)
    parameter("extparam", extend.joinToString("|"))
    parameter("count", count)
    parameter("refresh", refresh)
}