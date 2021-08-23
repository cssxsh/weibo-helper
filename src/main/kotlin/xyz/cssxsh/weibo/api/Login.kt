package xyz.cssxsh.weibo.api

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.data.*
import java.lang.*

private const val SUCCESS_CODE = 20000000

private const val NO_USE_CODE = 50114001

private const val USED_CODE = 50114002

private const val QRCODE_SIZE = 180

private const val CheckDelay = 3 * 1000L

private suspend inline fun <reified T> WeiboClient.data(
    url: String,
    crossinline block: HttpRequestBuilder.() -> Unit = {}
): T {
    return callback<LoginData>(url, block).run {
        check(code == SUCCESS_CODE) { toString() }
        WeiboClient.Json.decodeFromJsonElement(data)
    }
}

private fun location(html: String): String? {
    return html.substringAfter("location.replace(").substringBeforeLast(");")
        .removeSurrounding("'").removeSurrounding("\"")
        .takeIf { it.startsWith("http") }
}

suspend fun WeiboClient.qrcode(send: suspend (image: ByteArray) -> Unit): LoginResult {
    // Set Cookie
    download(PASSPORT_VISITOR)

    val code = data<LoginQrcode>(SSO_QRCODE_IMAGE) {
        parameter("entry", "weibo")
        parameter("size", QRCODE_SIZE)
        parameter("callback", "STK_${System.currentTimeMillis()}")
    }

    send(download(code.image))

    val token: LoginToken = supervisorScope {
        while (isActive) {
            val json = callback<LoginData>(SSO_QRCODE_CHECK) {
                parameter("entry", "weibo")
                parameter("qrid", code.id)
                parameter("callback", "STK_${System.currentTimeMillis()}")
            }
            // println(json)
            when (json.code) {
                SUCCESS_CODE -> {
                    return@supervisorScope WeiboClient.Json.decodeFromJsonElement(json.data)
                }
                NO_USE_CODE, USED_CODE -> {
                    delay(CheckDelay)
                }
                else -> {
                    throw IllegalStateException(json.msg)
                }
            }
        }
        throw CancellationException()
    }

    val flush = callback<LoginFlush>(SSO_LOGIN) {
        parameter("entry", "weibo")
        parameter("returntype", "TEXT")
        parameter("crossdomain", 1)
        parameter("cdult", 3)
        parameter("domain", "weibo.com")
        parameter("alt", token.alt)
        parameter("savestate", token.state)
        parameter("callback", "STK_${System.currentTimeMillis()}")
    }

    val url = flush.urls.first { it.startsWith(WEIBO_SSO_LOGIN) }

    return callback<LoginResult>(url) {}.also { info = it.info }
}

suspend fun WeiboClient.restore(): LoginResult {
    // Set Cookie
    var location: String? = INDEX_PAGE
    while (location != null) {
        location = location(text(location) {})
    }

    checkNotNull(srf) { "SRF Cookie 为空" }

    val token = data<LoginToken>(PASSPORT_VISITOR) {
        header(HttpHeaders.Referrer, PASSPORT_VISITOR)

        parameter("a", "restore")
        parameter("cb", "restore_back")
        parameter("from", "weibo")
        parameter("_rand", System.currentTimeMillis())
    }

    val html = text(SSO_LOGIN) {
        parameter("entry", "sso")
        parameter("returntype", "META")
        parameter("gateway", 1)
        parameter("alt", token.alt)
        parameter("savestate", token.state)
    }

    check(location(html)!!.startsWith(CROSS_DOMAIN)) { "跳转异常" }

    val flush = callback<LoginCrossFlush>(CROSS_DOMAIN) {
        parameter("action", "login")
        parameter("entry", "sso")
        parameter("r", INDEX_PAGE)
    }

    val url = flush.urls.first { it.startsWith(WEIBO_SSO_LOGIN) }

    return callback<LoginResult>(url) {}.also { info = it.info }
}

suspend fun WeiboClient.incarnate(): Int {
    val visitor = data<LoginVisitor>(PASSPORT_GEN_VISITOR) {
        parameter("cb", "restore_back")
        parameter("from", "weibo")
        parameter("_rand", System.currentTimeMillis())
    }

    val recover = if (visitor.new) 3 else 2
    val cookies = data<Map<String, String>>(PASSPORT_VISITOR) {
        header(HttpHeaders.Referrer, PASSPORT_VISITOR)

        parameter("a", "incarnate")
        parameter("t", visitor.tid)
        parameter("w", recover)
        parameter("c", visitor.confidence)
        parameter("gc", "")
        parameter("cb", "cross_domain")
        parameter("from", "weibo")
        parameter("_rand", System.currentTimeMillis())
    }

    load(LoginStatus().copy(cookies = cookies.map { (name, value) ->
        "${name.uppercase()}=${value}; Domain=.weibo.com; Path=/; HttpOnly; \$x-enc=RAW"
    }))

    return visitor.confidence
}