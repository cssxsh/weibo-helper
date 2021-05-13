package xyz.cssxsh.weibo.api

import io.ktor.client.request.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import xyz.cssxsh.weibo.*
import java.nio.charset.Charset

private val SSO_LOGIN_REGEX = """\?ticket=[^"]+""".toRegex()

private val LOGIN_RESULT = """(?<=\()(\{.+})(?=\);)""".toRegex()

@Serializable
data class LoginResult(
    @SerialName("result")
    val result: Boolean,
    @SerialName("userinfo")
    val userinfo: Userinfo
) {
    @Serializable
    data class Userinfo(
        @SerialName("displayname")
        val display: String,
        @SerialName("uniqueid")
        val uid: Long
    )
}

suspend fun WeiboClient.login() = useHttpClient { client ->
    client.get<ByteArray>(CROSS_DOMAIN) {
        parameter("action", "login")
        parameter("entry", "sso")
        parameter("r", INDEX_PAGE)
    }.toString(Charset.forName("GBK")).let { html ->
        SSO_LOGIN + requireNotNull(SSO_LOGIN_REGEX.find(html)) { "未找到登录参数 for $SSO_LOGIN" }.value
    }.let { url ->
        client.get<String>(url)
    }.let {
        Json.decodeFromString(LoginResult.serializer(), requireNotNull(LOGIN_RESULT.find(it)) { "未找到登录结果" }.value)
    }.also {
        loginResult = it
    }
}
