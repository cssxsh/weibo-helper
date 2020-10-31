package xyz.cssxsh.weibo

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.compression.*
import io.ktor.client.features.cookies.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking

class WeiboClient(cookies: Map<String, String> = emptyMap()) {

    private val cookiesStorage = AcceptAllCookiesStorage().apply {
        runBlocking {
            cookies.forEach { (name, value) ->
                addCookie("https://m.weibo.cn/", Cookie(name = name, value = value))
            }
        }
    }

    suspend fun <T> useHttpClient(block: suspend (HttpClient) -> T): T = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
        install(HttpTimeout) {
            socketTimeoutMillis = 30_000
            connectTimeoutMillis = 30_000
            requestTimeoutMillis = 60_000
        }
        ContentEncoding {
            gzip()
            deflate()
            identity()
        }
        install(HttpCookies) {
            default {
                storage = cookiesStorage
            }
        }
        BrowserUserAgent()
    }.use { block(it) }
}