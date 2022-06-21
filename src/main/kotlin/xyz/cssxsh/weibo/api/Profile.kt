package xyz.cssxsh.weibo.api

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.data.*

suspend fun WeiboClient.getUserInfo(uid: Long = info.uid): UserInfoData = temp(PROFILE_INFO) {
    header(HttpHeaders.Referrer, "https://www.weibo.com/u/${uid}")

    parameter("uid", uid)
}

suspend fun WeiboClient.getUserDetail(uid: Long = info.uid): UserDetail = temp(PROFILE_DETAIL) {
    header(HttpHeaders.Referrer, "https://www.weibo.com/u/${uid}")

    parameter("uid", uid)
}

suspend fun WeiboClient.getUserHistory(uid: Long = info.uid): HistoryInfo = temp(PROFILE_HISTORY) {
    header(HttpHeaders.Referrer, "https://www.weibo.com/u/${uid}")

    parameter("uid", uid)
}

suspend fun WeiboClient.getUserFollowers(uid: Long = info.uid, page: Int): UserGroupMembers {
    return temp(PROFILE_GROUP_MEMBERS) {
        header(HttpHeaders.Referrer, "https://weibo.com/u/page/follow/${uid}/followGroup")

        parameter("uid", uid)
        parameter("page", page)
    }
}

suspend fun WeiboClient.getGroupMembers(gid: Long, page: Int): UserGroupMembers = temp(PROFILE_GROUP_MEMBERS) {
    header(HttpHeaders.Referrer, "https://weibo.com/u/page/follow/${info.uid}/followGroup?tabid=${gid}")

    parameter("list_id", gid)
    parameter("page", page)
}

suspend fun WeiboClient.getGroupList(uid: Long): JsonArray = temp(PROFILE_GROUP_LIST) {
    header(HttpHeaders.Referrer, "https://weibo.com/u/${uid}/")

    parameter("uid", uid)
}

suspend fun WeiboClient.setGroup(users: List<Long>, dest: List<Long>, origin: List<Long> = emptyList()): SetResult {
    return temp(PROFILE_GROUP_SET) {
        method = HttpMethod.Post

        header(HttpHeaders.Referrer, "https://weibo.com/u/${info.uid}/")

        setBody(body = buildJsonObject {
            put("list_ids", dest.joinToString(","))
            put("origin_list_ids", origin.joinToString(","))
            put("uids", users.joinToString(","))
        })
        contentType(ContentType.Application.Json)
    }
}

suspend fun WeiboClient.setGroup(user: Long, group: Long) = setGroup(listOf(user), listOf(group))

suspend fun WeiboClient.follow(uid: Long): UserInfo = temp(FRIENDSHIPS_CREATE) {
    method = HttpMethod.Post

    header(HttpHeaders.Referrer, "https://weibo.com/u/${info.uid}/")

    setBody(body = buildJsonObject {
        put("friend_uid", uid)
        put("lpage", "profile")
        put("page", "profile")
    })
    contentType(ContentType.Application.Json)
}