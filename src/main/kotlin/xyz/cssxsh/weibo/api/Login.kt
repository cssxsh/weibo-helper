package xyz.cssxsh.weibo.api

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.decodeFromJsonElement
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.data.*
import java.lang.IllegalStateException

private const val SUCCESS_CODE = 20000000

private const val NO_USE = 50114001

private const val USED = 50114002

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

private val SSO_LOGIN_REGEX = """\?ticket=[^"]+""".toRegex()

private fun location(html: String): Url {
    return Url(html.substringAfter("location.replace(").substringBeforeLast(");").replace("""["']""".toRegex(), ""))
}

suspend fun WeiboClient.qrcode(send: suspend (image: ByteArray) -> Unit): LoginResult = withContext(Dispatchers.IO) {
    // Set Cookie
    get<ByteArray>(PASSPORT_VISITOR)

    val code = data<LoginQrcode>(SSO_QRCODE_IMAGE) {
        parameter("entry", "weibo")
        parameter("size", QRCODE_SIZE)
        parameter("callback", "STK_${System.currentTimeMillis()}")
    }

    send(get(Url(code.image).copy(protocol = URLProtocol.HTTPS)))

    lateinit var token: LoginToken

    while (isActive) {
        val json = callback<LoginData>(SSO_QRCODE_CHECK) {
            parameter("entry", "weibo")
            parameter("qrid", code.id)
            parameter("callback", "STK_${System.currentTimeMillis()}")
        }
        // println(json)
        when (json.code) {
            SUCCESS_CODE -> {
                token = WeiboClient.Json.decodeFromJsonElement(json.data)
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

    val url = callback<LoginFlush>(SSO_LOGIN) {
        parameter("entry", "weibo")
        parameter("returntype", "TEXT")
        parameter("crossdomain", 1)
        parameter("cdult", 3)
        parameter("domain", "weibo.com")
        parameter("alt", token.alt)
        parameter("savestate", token.state)
        parameter("callback", "STK_${System.currentTimeMillis()}")
    }.urls.first { SSO_LOGIN_REGEX in it }

    return@withContext callback<LoginResult>(url).also { info = it.info }
}

suspend fun WeiboClient.restore(): LoginResult {
    // Set Cookie
    runCatching {
        get<String>(INDEX_PAGE)
    }.mapCatching {
        // println(location(it))
        get<String>(location(it))
    }.mapCatching {
        // println(location(it))
        get<String>(location(it))
    }

    check(srf.isNotBlank())
    // println(srf)

    val token = data<LoginToken>(PASSPORT_VISITOR) {
        header(HttpHeaders.Referrer, PASSPORT_VISITOR)

        parameter("a", "restore")
        parameter("cb", "restore_back")
        parameter("from", "weibo")
        parameter("_rand", System.currentTimeMillis())
    }

    get<String>(SSO_LOGIN) {
        parameter("entry", "sso")
        parameter("returntype", "META")
        parameter("gateway", 1)
        parameter("alt", token.alt)
        parameter("savestate", token.state)
    }

    val text = get<String>(CROSS_DOMAIN) {
        parameter("action", "login")
        parameter("entry", "sso")
        parameter("r", INDEX_PAGE)
    }
    val url = WEIBO_SSO_LOGIN + requireNotNull(SSO_LOGIN_REGEX.find(text)) { "未找到登录参数 for $WEIBO_SSO_LOGIN" }.value
    return callback<LoginResult>(url).also { info = it.info }
}

suspend fun WeiboClient.incarnate(): Int {
    val visitor = data<LoginVisitor>(PASSPORT_GEN_VISITOR) {
        parameter("cb", "restore_back")
        parameter("from", "weibo")
        parameter("_rand", System.currentTimeMillis())
    }

    val recover = if (visitor.new) 3 else 2
    val cookie = data<LoginCookie>(PASSPORT_VISITOR) {
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

    val cookies = listOf(
        "SUB=${cookie.sub}; Domain=.weibo.com; Path=/; HttpOnly; \$x-enc=RAW",
        "SUBP=${cookie.subp}; Domain=.weibo.com; Path=/; HttpOnly; \$x-enc=RAW",
    )
    load(LoginStatus().copy(cookies = cookies))
    return visitor.confidence
}