package xyz.cssxsh.weibo

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import kotlinx.serialization.json.*
import xyz.cssxsh.weibo.data.*
import kotlin.coroutines.*

public open class WeiboClient(
    public val ignore: suspend (Throwable) -> Boolean = DefaultIgnore
) : CoroutineScope, Closeable {
    override val coroutineContext: CoroutineContext
        get() = client.coroutineContext

    override fun close(): Unit = client.close()

    protected val cookies: List<String>
        get() = storage.container.filter { it.expires != null }.map(::renderSetCookieHeader)

    public fun status(): LoginStatus = LoginStatus(info, cookies)

    public fun load(status: LoginStatus): Boolean = runBlocking(coroutineContext) {
        info = status.info
        storage.mutex.withLock {
            storage.container.addAll(status.cookies.map(::parseServerSetCookieHeader))
        }
    }

    protected open val storage: CookiesStorage = AcceptAllCookiesStorage()

    @PublishedApi
    internal open var info: LoginUserInfo = LoginUserInfo("", 0)

    @PublishedApi
    internal val xsrf: Cookie? get() = storage.container["XSRF-TOKEN"]

    @PublishedApi
    internal val srf: Cookie? get() = storage.container["SRF"]

    @PublishedApi
    internal val wbpsess: Cookie? get() = storage.container["WBPSESS"]

    protected open val timeout: Long = 30_000 // attr(open) ok ?

    protected open val client: HttpClient = HttpClient(OkHttp) {
        install(HttpTimeout) {
            socketTimeoutMillis = timeout
            connectTimeoutMillis = timeout
            requestTimeoutMillis = null
        }
        install(HttpCookies) {
            storage = this@WeiboClient.storage
        }
        Charsets {
            responseCharsetFallback = Charsets.GBK
        }
        BrowserUserAgent()
        ContentEncoding()
        defaultRequest {
            header("x-xsrf-token", xsrf?.value)
        }
        expectSuccess = true
    }

    public companion object {
        public val Json: Json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            isLenient = true
        }

        public val DefaultIgnore: suspend (Throwable) -> Boolean = { it is IOException }
    }

    protected open val max: Int = 32

    public suspend fun <T> useHttpClient(block: suspend (HttpClient) -> T): T = supervisorScope {
        var count = 0
        var cause: Throwable? = null
        while (isActive) {
            try {
                return@supervisorScope block(client)
            } catch (throwable: Throwable) {
                cause = throwable
                count++
                if (count > max || ignore(throwable).not()) throw throwable
            }
        }
        throw CancellationException(null, cause)
    }
}