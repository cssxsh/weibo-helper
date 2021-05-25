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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*
import java.io.IOException
import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty

class WeiboClient(val ignore: suspend (exception: Throwable) -> Boolean = DefaultIgnore) {
    constructor(status: LoginStatus, ignore: suspend (exception: Throwable) -> Boolean = DefaultIgnore): this(ignore) {
        info = status.info
        token = status.token
        cookiesStorage.container.addAll(status.cookies.map(::parseServerSetCookieHeader))
    }

    fun status(): LoginStatus = runBlocking {
        cookiesStorage.get(Url(SSO_LOGIN)) // cleanup
        cookiesStorage.mutex.withLock {
            LoginStatus(token, info, cookiesStorage.container.map(::renderSetCookieHeader))
        }
    }

    private inline fun <reified T: Any, reified R> reflect() = ReadOnlyProperty<T, R> { thisRef, property ->
        thisRef::class.java.getDeclaredField(property.name).apply { isAccessible = true }.get(thisRef) as R
    }

    private val AcceptAllCookiesStorage.mutex: Mutex by reflect()

    private val AcceptAllCookiesStorage.container: MutableList<Cookie> by reflect()

    private val cookiesStorage = AcceptAllCookiesStorage()

    internal var info: LoginUserInfo by Delegates.notNull()

    internal var token: String by Delegates.notNull()

    private fun client() = HttpClient(OkHttp) {
        Json {
            serializer = KotlinxSerializer(Json)
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
        val Json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            isLenient = true
            allowStructuredMapKeys = true
        }

        val DefaultIgnore: suspend (Throwable) -> Boolean = { it is IOException }
    }

    suspend fun <T> useHttpClient(block: suspend (HttpClient) -> T): T = client().use {
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