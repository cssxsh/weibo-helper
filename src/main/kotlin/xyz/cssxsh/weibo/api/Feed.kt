package xyz.cssxsh.weibo.api

import io.ktor.client.request.*
import io.ktor.http.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.data.*

suspend fun WeiboClient.getFeedGroups(isNewSegment: Boolean = true, fetchHot: Boolean = true): UserGroupData {
    return json(FEED_ALL_GROUPS) {
        header(HttpHeaders.Referrer, INDEX_PAGE)

        parameter("is_new_segment", isNewSegment.toInt())
        parameter("fetch_hot", fetchHot.toInt())
    }
}

suspend fun WeiboClient.getGroupsTimeline(
    gid: Long,
    count: Int = PAGE_SIZE,
    refresh: Boolean = true,
    since: Long? = null,
    max: Long? = null,
    fast: Boolean? = null
): TimelineData = json(FEED_GROUPS_TIMELINE) {
    header(HttpHeaders.Referrer, "https://weibo.com/mygroups?gid=$gid")

    parameter("list_id", gid)
    parameter("since_id", since)
    parameter("max_id", max)
    parameter("refresh", refresh.toInt())
    parameter("fast_refresh", fast)
    parameter("count", count)
}

suspend fun WeiboClient.getUnreadTimeline(
    gid: Long,
    count: Int = PAGE_SIZE,
    refresh: Boolean = true,
    since: Long? = null,
    max: Long? = null,
    fast: Boolean? = null
): TimelineData = json(FEED_UNREAD_FRIENDS_TIMELINE) {
    header(HttpHeaders.Referrer, "https://weibo.com/mygroups?gid=$gid")

    parameter("list_id", gid)
    parameter("since_id", since)
    parameter("max_id", max)
    parameter("refresh", refresh.toInt())
    parameter("fast_refresh", fast?.toInt())
    parameter("count", count)
}

suspend fun WeiboClient.getFriendsTimeline(
    gid: Long,
    count: Int = PAGE_SIZE,
    refresh: Boolean = true,
    since: Long? = null,
    max: Long? = null,
    fast: Boolean? = null
): TimelineData = json(FEED_FRIENDS_TIMELINE) {
    header(HttpHeaders.Referrer, "https://weibo.com/mygroups?gid=$gid")

    parameter("list_id", gid)
    parameter("since_id", since)
    parameter("max_id", max)
    parameter("refresh", refresh.toInt())
    parameter("fast_refresh", fast?.toInt())
    parameter("count", count)
}

suspend fun WeiboClient.getHotTimeline(
    gid: Long,
    max: Long? = null,
    extend: List<String> = listOf("discover", "new_feed"),
    count: Int = PAGE_SIZE,
    refresh: Boolean = false,
): TimelineData = json(FEED_HOT_TIMELINE) {
    header(HttpHeaders.Referrer, "https://weibo.com/hot/list/$gid")

    parameter("group_id", gid)
    parameter("containerid", gid)
    parameter("max_id", max)
    parameter("extparam", extend.joinToString("|"))
    parameter("count", count)
    parameter("refresh", refresh.toInt())
}

suspend fun WeiboClient.getTimeline(group: UserGroup): TimelineData = when (group.type) {
    UserGroupType.USER, UserGroupType.QUIETLY -> {
        getGroupsTimeline(group.gid)
    }
    UserGroupType.ALL -> {
        getUnreadTimeline(group.gid)
    }
    UserGroupType.FILTER, UserGroupType.MUTUAL, UserGroupType.GROUP -> {
        getFriendsTimeline(group.gid)
    }
    UserGroupType.SYSTEM -> {
        getHotTimeline(group.gid)
    }
}