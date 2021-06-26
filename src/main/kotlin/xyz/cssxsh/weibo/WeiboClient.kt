package xyz.cssxsh.weibo

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.compression.*
import io.ktor.client.features.cookies.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty

class WeiboClient(val ignore: suspend (exception: Throwable) -> Boolean = DefaultIgnore) {
    constructor(status: LoginStatus, ignore: suspend (exception: Throwable) -> Boolean = DefaultIgnore) : this(ignore) {
        info = status.info
        storage.container.addAll(status.cookies.map(::parseServerSetCookieHeader))
    }

    fun status(): LoginStatus = runBlocking {
        storage.get(Url(SSO_LOGIN)) // cleanup
        storage.mutex.withLock {
            LoginStatus(info, storage.container.filter { it.expires != null }.map(::renderSetCookieHeader))
        }
    }

    fun load(status: LoginStatus) = runBlocking {
        info = status.info
        storage.mutex.withLock {
            storage.container.addAll(status.cookies.map(::parseServerSetCookieHeader))
        }
    }

    private inline fun <reified T : Any, reified R> reflect() = ReadOnlyProperty<T, R> { thisRef, property ->
        thisRef::class.java.getDeclaredField(property.name).apply { isAccessible = true }.get(thisRef) as R
    }

    private val AcceptAllCookiesStorage.mutex: Mutex by reflect()

    private val AcceptAllCookiesStorage.container: MutableList<Cookie> by reflect()

    private val storage = AcceptAllCookiesStorage()

    internal var info: LoginUserInfo by Delegates.notNull()

    internal val xsrf: String get() = storage.container.first { it.name == "XSRF-TOKEN" }.value

    internal val srf: String get() = storage.container.first { it.name == "SRF" }.value

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
            storage = this@WeiboClient.storage
        }
        install(HttpPlainText) {
            responseCharsetFallback = ChineseCharset
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

        private val IgnoreRegex = """Expected \d+, actual \d+""".toRegex()

        val DefaultIgnore: suspend (Throwable) -> Boolean = {
            it is IOException || it is HttpRequestTimeoutException || it.message.orEmpty().matches(IgnoreRegex)
        }
    }

    suspend fun <T> useHttpClient(block: suspend (HttpClient) -> T): T = supervisorScope {
        while (isActive) {
            runCatching {
                client().use { block(it) }
            }.onSuccess {
                return@supervisorScope it
            }.onFailure {
                if (ignore(it).not()) throw it
            }
        }
        throw CancellationException()
    }
}