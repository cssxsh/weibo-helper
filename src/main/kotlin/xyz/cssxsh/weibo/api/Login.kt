package xyz.cssxsh.weibo.api

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.decodeFromJsonElement
import xyz.cssxsh.weibo.WeiboClient
import xyz.cssxsh.weibo.data.*
import java.lang.IllegalStateException
import java.nio.charset.Charset
import kotlin.time.seconds

private const val SUCCESS_CODE = 20000000

private const val NO_USE = 50114001

private const val USED = 50114002

private val CheckDelay = (3).seconds

private inline fun <reified T> String.readCallback(): T {
    val text = substringAfter('(').substringBeforeLast(')')
    return WeiboClient.Json.decodeFromString(text)
}

private fun QrcodeData.qrcode(): Qrcode {
    check(code == SUCCESS_CODE) { msg }
    return WeiboClient.Json.decodeFromJsonElement(data)
}

private fun QrcodeData.token(): QrcodeToken {
    check(code == SUCCESS_CODE) { msg }
    return WeiboClient.Json.decodeFromJsonElement(data)
}

private val SSO_LOGIN_REGEX = """\?ticket=[^"]+""".toRegex()

suspend fun WeiboClient.qrcode(send: suspend (image: ByteArray) -> Unit): LoginResult {
    val code = useHttpClient { client ->
        client.get<String>(SSO_QRCODE_IMAGE) {
            parameter("entry", "weibo")
            parameter("size", 180)
            parameter("callback", "STK_${System.currentTimeMillis()}")
        }.readCallback<QrcodeData>().qrcode()
    }
    // println(cookiesStorage.get(Url(SSO_LOGIN)))

    val image = useHttpClient { client ->
        client.get<ByteArray>(Url(code.image).copy(protocol = URLProtocol.HTTPS))
    }
    send(image)

    while (currentCoroutineContext().isActive) {
        val json = useHttpClient { client ->
            client.get<String>(SSO_QRCODE_CHECK) {
                parameter("entry", "weibo")
                parameter("qrid", code.id)
                parameter("callback", "STK_${System.currentTimeMillis()}")
            }.readCallback<QrcodeData>()
        }
        // println(json)
        when (json.code) {
            SUCCESS_CODE -> {
                token = json.token().alt
                break
            }
            NO_USE, USED -> {
                delay(CheckDelay)
            }
            else -> {
                throw IllegalStateException(json.msg)
            }
        }
    }
    val url = useHttpClient { client ->
        client.get<String>(SSO_LOGIN) {
            parameter("entry", "weibo")
            parameter("returntype", "TEXT")
            parameter("crossdomain", 1)
            parameter("cdult", 3)
            parameter("domain", "weibo.com")
            parameter("alt", token)
            parameter("savestate", 30)
            parameter("callback", "STK_${System.currentTimeMillis()}")
        }
    }.readCallback<LoginFlush>().urls.first { SSO_LOGIN_REGEX in it }

    return useHttpClient { client -> client.get<String>(url) }.readCallback<LoginResult>().also { info = it.userinfo }
}

suspend fun WeiboClient.flush() = useHttpClient { client ->
    val text = client.get<ByteArray>(CROSS_DOMAIN) {
        parameter("action", "login")
        parameter("entry", "sso")
        parameter("r", INDEX_PAGE)
    }.toString(Charset.forName("GBK"))

    val url = WSSO_LOGIN + requireNotNull(SSO_LOGIN_REGEX.find(text)) { "未找到登录参数 for $WSSO_LOGIN" }.value
    client.get<String>(url).readCallback<LoginResult>().also { info = it.userinfo }
}

fun WeiboClient.status(): LoginStatus = runBlocking {
    LoginStatus(token, info, cookiesStorage.get(Url(SSO_LOGIN)).map { renderSetCookieHeader(it) })
}