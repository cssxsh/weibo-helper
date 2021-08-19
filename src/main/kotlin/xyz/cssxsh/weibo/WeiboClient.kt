package xyz.cssxsh.weibo

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.compression.*
import io.ktor.client.features.cookies.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import kotlinx.serialization.json.*
import xyz.cssxsh.weibo.data.*
import java.io.IOException
import kotlin.coroutines.*
import kotlin.coroutines.cancellation.*
import kotlin.properties.*

@OptIn(KtorExperimentalAPI::class)
open class WeiboClient(val ignore: suspend (Throwable) -> Boolean = DefaultIgnore) : CoroutineScope, Closeable {
    override val coroutineContext: CoroutineContext
        get() = client.coroutineContext

    override fun close() = client.close()

    protected val cookies get() = storage.container.filter { it.expires != null }.map(::renderSetCookieHeader)

    fun status() = LoginStatus(info, cookies)

    fun load(status: LoginStatus) = runBlocking {
        info = status.info
        storage.mutex.withLock {
            storage.container.addAll(status.cookies.map(::parseServerSetCookieHeader))
        }
    }

    protected val storage = AcceptAllCookiesStorage()

    internal open var info: LoginUserInfo by Delegates.notNull()

    internal val xsrf get() = storage.container["XSRF-TOKEN"]

    internal val srf get() = storage.container["SRF"]

    protected open val timeout: Long = 30_000 // attr(open) ok ?

    protected open val client = HttpClient(OkHttp) {
        install(HttpTimeout) {
            socketTimeoutMillis = timeout
            connectTimeoutMillis = timeout
            requestTimeoutMillis = timeout
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
        defaultRequest {
            header("x-xsrf-token", xsrf?.value)
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

    protected open val max = 20

    suspend fun <T> useHttpClient(block: suspend (HttpClient) -> T): T = supervisorScope {
        var count = 0
        while (isActive) {
            runCatching {
                block(client)
            }.onSuccess {
                return@supervisorScope it
            }.onFailure {
                if (++count > max || ignore(it).not()) throw it
            }
        }
        throw CancellationException(null)
    }
}