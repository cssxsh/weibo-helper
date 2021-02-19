package xyz.cssxsh.weibo.api

import io.ktor.client.request.*
import io.ktor.http.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.data.*

suspend fun WeiboClient.getFeedGroups(
    isNewSegment: Boolean = true,
    fetchHot: Boolean = true
): UserGroupData = useHttpClient { client ->
    client.get(WeiboApi.ALL_GROUPS) {
        header(HttpHeaders.Referrer, WeiboApi.INDEX_PAGE)

        parameter("is_new_segment", if (isNewSegment) 1 else 0)
        parameter("fetch_hot", if (fetchHot) 1 else 0)
    }
}

enum class TimelineType(val url: String) {
    UNREAD_FRIENDS(url = WeiboApi.UNREAD_FRIENDS_TIMELINE),
    GROUPS(url = WeiboApi.GROUPS_TIMELINE),
    FRIENDS(url = WeiboApi.FRIENDS_TIMELINE)
}

internal suspend fun WeiboClient.getTimeline(
    listId: Long,
    sinceId: Long,
    maxId: Long?,
    count: Int,
    refresh: Int,
    fastRefresh: Int?,
    url: String
): TimelineData = useHttpClient { client ->
    client.get(url) {
        header(HttpHeaders.Referrer, "https://weibo.com/mygroups?gid=$listId")

        parameter("list_id", listId)
        parameter("since_id", sinceId)
        parameter("max_id", maxId)
        parameter("refresh", refresh)
        parameter("fast_refresh", fastRefresh)
        parameter("count", count)
    }
}

suspend fun WeiboClient.getTimeline(
    listId: Long,
    sinceId: Long = 0,
    maxId: Long? = null,
    count: Int = WeiboApi.STATUSES_PAGE_SIZE,
    refresh: Int = 4,
    fastRefresh: Int? = null,
    type: TimelineType = TimelineType.UNREAD_FRIENDS
) = getTimeline(
    listId = listId,
    maxId = maxId,
    sinceId = sinceId,
    count = count,
    refresh = refresh,
    fastRefresh = fastRefresh,
    url = type.url
)