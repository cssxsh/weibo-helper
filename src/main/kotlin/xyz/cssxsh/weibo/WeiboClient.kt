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
import io.ktor.util.date.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.internal.http2.StreamResetException
import xyz.cssxsh.weibo.api.*
import java.io.EOFException
import java.net.ConnectException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

class WeiboClient(val ignore: suspend (exception: Throwable) -> Boolean = DefaultIgnore) {
    constructor(init: List<HttpCookie>, ignore: suspend (exception: Throwable) -> Boolean = DefaultIgnore) : this(ignore) {
        runBlocking {
            loadCookies(init)
        }
    }

    private val cookiesStorage = AcceptAllCookiesStorage()

    internal lateinit var loginResult: LoginResult

    @Suppress("unused")
    suspend fun loadCookies(list: List<HttpCookie>) = list.forEach {
        cookiesStorage.addCookie(SINA_LOGIN, Cookie(
            name = it.name,
            value = it.value,
            encoding = CookieEncoding.RAW,
            expires = it.expirationDate?.run { GMTDate(times(1_000).toLong()) },
            path = it.path,
            domain = it.domain,
            secure = it.secure,
            httpOnly = it.httpOnly
        ))
    }

    private fun httpClient() = HttpClient(OkHttp) {
        Json {
            serializer = KotlinxSerializer
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
        private val KotlinxSerializer = KotlinxSerializer(Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            isLenient = true
            allowStructuredMapKeys = true
        })

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
    ): T = httpClient().use {
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