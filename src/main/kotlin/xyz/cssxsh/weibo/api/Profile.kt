package xyz.cssxsh.weibo.api

import io.ktor.client.request.*
import io.ktor.http.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.data.*

suspend fun WeiboClient.getUserInfo(
    uid: Long? = null
): UserInfoData = useHttpClient { client ->
    client.get(WeiboApi.PROFILE_INFO) {
        header(HttpHeaders.Referrer, "https://www.weibo.com/u/${uid}")

        parameter("uid", uid ?: loginResult.userinfo.uid)
    }
}

suspend fun WeiboClient.getUserDetail(
    uid: Long? = null
): UserDetailData = useHttpClient { client ->
    client.get(WeiboApi.PROFILE_DETAIL) {
        header(HttpHeaders.Referrer, "https://www.weibo.com/u/${uid}")

        parameter("uid", uid ?: loginResult.userinfo.uid)
    }
}

suspend fun WeiboClient.getUserHistory(
    uid: Long? = null
): HistoryData = useHttpClient { client ->
    client.get(WeiboApi.PROFILE_HISTORY) {
        header(HttpHeaders.Referrer, "https://www.weibo.com/u/${uid}")

        parameter("uid", uid ?: loginResult.userinfo.uid)
    }
}