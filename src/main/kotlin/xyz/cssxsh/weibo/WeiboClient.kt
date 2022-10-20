package xyz.cssxsh.weibo

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import kotlinx.serialization.json.*
import xyz.cssxsh.weibo.data.*
import java.io.IOException
import kotlin.coroutines.*

open class WeiboClient(val ignore: suspend (Throwable) -> Boolean = DefaultIgnore) : CoroutineScope, Closeable {
    override val coroutineContext: CoroutineContext
        get() = client.coroutineContext

    override fun close() = client.close()

    protected val cookies get() = storage.container.filter { it.expires != null }.map(::renderSetCookieHeader)

    fun status() = LoginStatus(info, cookies)

    fun load(status: LoginStatus) = runBlocking(coroutineContext) {
        info = status.info
        storage.mutex.withLock {
            storage.container.addAll(status.cookies.map(::parseServerSetCookieHeader))
        }
    }

    protected val storage = AcceptAllCookiesStorage()

    internal open var info: LoginUserInfo = LoginUserInfo("", 0)

    internal val xsrf get() = storage.container["XSRF-TOKEN"]

    internal val srf get() = storage.container["SRF"]

    internal val wbpsess get() = storage.container["WBPSESS"]

    protected open val timeout: Long = 30_000 // attr(open) ok ?

    protected open val client = HttpClient(OkHttp) {
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

    companion object {
        val Json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            isLenient = true
        }

        val DefaultIgnore: suspend (Throwable) -> Boolean = { it is IOException }
    }

    protected open val max = 32

    suspend fun <T> useHttpClient(block: suspend (HttpClient) -> T): T = supervisorScope {
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