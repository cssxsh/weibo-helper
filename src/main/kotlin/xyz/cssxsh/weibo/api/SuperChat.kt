package xyz.cssxsh.weibo.api

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.data.*

public suspend fun WeiboClient.getSuperChatData(
    id: String,
): SuperChatData = temp("https://m.weibo.cn/api/container/getIndex") {
    header(HttpHeaders.Referrer, "https://m.weibo.cn/p/${id}/super_index")

    parameter("jumpfrom", "weibocom")
    parameter("sudaref", "login.sina.com.cn")
    parameter("containerid", "${id}_-_feed")
}

public suspend fun WeiboClient.getSuperChatHome(
    name: String,
): String {
    val url = useHttpClient { http ->
        http.head("https://huati.weibo.com/k/$name").request.url
    }
    return url.encodedPath.substringAfterLast('/')
}

