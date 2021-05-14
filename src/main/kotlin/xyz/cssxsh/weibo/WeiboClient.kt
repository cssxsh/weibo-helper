package xyz.cssxsh.weibo

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.compression.*
import io.ktor.client.features.cookies.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.http.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.internal.http2.StreamResetException
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*
import java.io.EOFException
import java.net.ConnectException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

class WeiboClient(val ignore: suspend (exception: Throwable) -> Boolean = DefaultIgnore) {
    constructor(status: LoginStatus, ignore: suspend (exception: Throwable) -> Boolean = DefaultIgnore): this(ignore) {
        info = status.info
        token = status.token
        runBlocking {
            status.cookies.forEach { header ->
                cookiesStorage.addCookie(SSO_LOGIN, parseServerSetCookieHeader(header))
            }
        }
    }

    internal val cookiesStorage = AcceptAllCookiesStorage()

    internal lateinit var info: LoginUserInfo

    internal lateinit var token: String

    private fun client() = HttpClient(OkHttp) {
        Json {
            serializer = KotlinxSerializer(json)
        }
        install(HttpTimeout) {
            socketTimeoutMillis = 5_000
            connectTimeoutMillis = 5_000
            requestTimeoutMillis = 5_000
        }
        install(HttpCookies) {
            storage = cookiesStorage
        }
        BrowserUserAgent()
        ContentEncoding {
            gzip()
            deflate()
            identity()
        }
    }

    companion object {
        val json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            isLenient = true
            allowStructuredMapKeys = true
        }

        private val DefaultIgnore: suspend (exception: Throwable) -> Boolean = { throwable ->
            when (throwable) {
                is SSLException,
                is EOFException,
                is ConnectException,
                is SocketTimeoutException,
                is HttpRequestTimeoutException,
                is StreamResetException,
                is UnknownHostException,
                -> {
                    true
                }
                else -> when (throwable.message) {
                    "Required SETTINGS preface not received" -> true
                    else -> false
                }
            }
        }
    }

    suspend fun <T> useHttpClient(
        block: suspend (HttpClient) -> T
    ): T = client().use {
        var result: T? = null
        while (result === null) {
            result = runCatching {
                block(it)
            }.onFailure {
                if (ignore(it).not()) throw it
            }.getOrNull()
        }
        result
    }
}