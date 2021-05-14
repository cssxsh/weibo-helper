package xyz.cssxsh.weibo.api

import io.ktor.client.request.*
import io.ktor.http.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.data.*

suspend fun WeiboClient.getUserInfo(
    uid: Long = info.uid
): UserInfoData = temp(PROFILE_INFO) {
    header(HttpHeaders.Referrer, "https://www.weibo.com/u/${uid}")

    parameter("uid", uid)
}

suspend fun WeiboClient.getUserDetail(
    uid: Long = info.uid
): UserDetail = temp(PROFILE_DETAIL) {
    header(HttpHeaders.Referrer, "https://www.weibo.com/u/${uid}")

    parameter("uid", uid)
}

suspend fun WeiboClient.getUserHistory(
    uid: Long = info.uid
): HistoryInfo = temp(PROFILE_HISTORY) {
    header(HttpHeaders.Referrer, "https://www.weibo.com/u/${uid}")

    parameter("uid", uid)
}