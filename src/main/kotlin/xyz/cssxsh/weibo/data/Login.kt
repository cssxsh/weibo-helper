package xyz.cssxsh.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class QrcodeData(
    @SerialName("data")
    val `data`: JsonElement,
    @SerialName("msg")
    val msg: String,
    @SerialName("retcode")
    val code: Int
)

@Serializable
data class Qrcode(
    @SerialName("image")
    val image: String,
    @SerialName("qrid")
    val id: String
)

@Serializable
data class QrcodeToken(
    @SerialName("alt")
    val alt: String
)

@Serializable
data class LoginResult(
    @SerialName("result")
    val result: Boolean,
    @SerialName("userinfo")
    val userinfo: LoginUserInfo
)

@Serializable
data class LoginUserInfo(
    @SerialName("displayname")
    val display: String,
    @SerialName("uniqueid")
    val uid: Long
)

@Serializable
data class LoginFlush(
    @SerialName("crossDomainUrlList")
    val urls: List<String>,
    @SerialName("nick")
    val nick: String,
    @SerialName("retcode")
    val code: String,
    @SerialName("uid")
    val uid: String
)

@Serializable
data class LoginStatus(
    @SerialName("alt")
    val token: String = "",
    @SerialName("info")
    val info: LoginUserInfo = LoginUserInfo("", 0),
    @SerialName("cookies")
    val cookies: List<String> = emptyList(),
)