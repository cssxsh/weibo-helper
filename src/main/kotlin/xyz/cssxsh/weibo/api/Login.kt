package xyz.cssxsh.weibo.api

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.data.*
import java.lang.*

const val SUCCESS_CODE = 20000000

const val NO_USE_CODE = 50114001

const val USED_CODE = 50114002

const val QRCODE_SIZE = 180

const val CheckDelay = 3 * 1000L

suspend inline fun <reified T> WeiboClient.data(url: String, crossinline block: HttpRequestBuilder.() -> Unit): T {
    return with(callback<LoginData>(url, block)) {
        check(code == SUCCESS_CODE) { toString() }
        WeiboClient.Json.decodeFromJsonElement(data)
    }
}

private fun location(html: String): String? {
    return html.substringAfter("location.replace(").substringBeforeLast(");")
        .removeSurrounding("'").removeSurrounding("\"")
        .takeIf { it.startsWith("http") }
}

private suspend fun WeiboClient.login(urls: List<String>): LoginResult {
    val result = callback<LoginResult>(urls.first { it.startsWith(WEIBO_SSO_LOGIN) }) {
        header(HttpHeaders.Host, url.host)
        header(HttpHeaders.Referrer, INDEX_PAGE)

        parameter("action", "login")
        parameter("callback", "STK_${System.currentTimeMillis()}")
    }

    info = result.info

    return result
}

suspend fun WeiboClient.qrcode(send: suspend (qrcode: String) -> Unit): LoginResult {
    // Set Cookie
    download(PASSPORT_VISITOR)

    val qrcode = data<LoginQrcode>(SSO_QRCODE_IMAGE) {
        header(HttpHeaders.Host, url.host)
        header(HttpHeaders.Referrer, INDEX_PAGE)

        parameter("entry", "sinawap")
        parameter("size", QRCODE_SIZE)
        parameter("callback", "STK_${System.currentTimeMillis()}")
    }

    send(Url(qrcode.image).copy(protocol = URLProtocol.HTTPS).toString())

    val token: LoginToken = supervisorScope {
        while (isActive) {
            val json = callback<LoginData>(SSO_QRCODE_CHECK) {
                header(HttpHeaders.Host, url.host)
                header(HttpHeaders.Referrer, INDEX_PAGE)

                parameter("entry", "sinawap")
                parameter("qrid", qrcode.id)
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
                    throw IllegalStateException(json.message)
                }
            }
        }
        throw CancellationException()
    }

    val flush = callback<LoginFlush>(SSO_LOGIN) {
        header(HttpHeaders.Host, url.host)
        header(HttpHeaders.Referrer, INDEX_PAGE)

        parameter("entry", "weibo")
        parameter("returntype", "TEXT")
        parameter("crossdomain", 1)
        parameter("cdult", 3)
        parameter("domain", "weibo.com")
        parameter("alt", token.alt)
        parameter("savestate", token.state)
        parameter("callback", "STK_${System.currentTimeMillis()}")
    }

    return login(urls = flush.urls)
}

suspend fun WeiboClient.restore(): LoginResult {
    // Set Cookie
    var location: String? = INDEX_PAGE
    while (location != null) {
        location = location(text(location) {})
    }

    checkNotNull(srf) { "SRF Cookie 为空" }

    val token = data<LoginToken>(PASSPORT_VISITOR) {
        header(HttpHeaders.Host, url.host)
        header(HttpHeaders.Referrer, PASSPORT_VISITOR)

        parameter("a", "restore")
        parameter("cb", "restore_back")
        parameter("from", "weibo")
        parameter("_rand", System.currentTimeMillis())
    }

    val html = text(SSO_LOGIN) {
        header(HttpHeaders.Host, url.host)
        header(HttpHeaders.Referrer, INDEX_PAGE)

        parameter("entry", "sso")
        parameter("returntype", "META")
        parameter("gateway", 1)
        parameter("alt", token.alt)
        parameter("savestate", token.state)
    }

    check(location(html).orEmpty().startsWith(CROSS_DOMAIN)) { "跳转异常" }

    val flush = callback<LoginCrossFlush>(CROSS_DOMAIN) {
        header(HttpHeaders.Host, url.host)
        header(HttpHeaders.Referrer, INDEX_PAGE)

        parameter("action", "login")
        parameter("entry", "sso")
        parameter("r", INDEX_PAGE)
    }

    return login(urls = flush.urls)
}

suspend fun WeiboClient.incarnate(): Int {
    val visitor = data<LoginVisitor>(PASSPORT_GEN_VISITOR) {
        header(HttpHeaders.Host, url.host)
        header(HttpHeaders.Referrer, PASSPORT_VISITOR)

        parameter("cb", "restore_back")
        parameter("from", "weibo")
        parameter("_rand", System.currentTimeMillis())
    }

    val recover = if (visitor.new) 3 else 2
    val cookies = data<Map<String, String>>(PASSPORT_VISITOR) {
        header(HttpHeaders.Host, url.host)
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

    load(status = LoginStatus(cookies = cookies.map { (name, value) ->
        "${name.uppercase()}=${value}; Domain=.weibo.com; Path=/; HttpOnly; \$x-enc=RAW"
    }))

    return visitor.confidence
}