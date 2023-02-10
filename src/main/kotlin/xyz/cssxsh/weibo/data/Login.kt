package xyz.cssxsh.weibo.data

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
public data class LoginData(
    @SerialName("data")
    val `data`: JsonElement,
    @SerialName("msg")
    val message: String,
    @SerialName("retcode")
    val code: Int
)

@Serializable
public data class LoginQrcode(
    @SerialName("image")
    val image: String,
    @SerialName("qrid")
    val id: String
)

@Serializable
public data class LoginToken(
    @SerialName("alt")
    val alt: String,
    @SerialName("savestate")
    val state: Int = 30
)

@Serializable
public data class LoginVisitor(
    @SerialName("tid")
    val tid: String,
    @SerialName("new_tid")
    val new: Boolean = false,
    @SerialName("confidence")
    val confidence: Int = 100
)

@Serializable
public data class LoginResult(
    @SerialName("result")
    val result: Boolean,
    @SerialName("userinfo")
    val info: LoginUserInfo
)

@Serializable
public data class LoginUserInfo(
    @SerialName("displayname")
    val display: String = "",
    @SerialName("uniqueid")
    val uid: Long = 0
)

@Serializable
public data class LoginFlush(
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
public data class LoginCrossFlush(
    @SerialName("arrURL")
    val urls: List<String>,
    @SerialName("retcode")
    val code: String
)

@Serializable
public data class LoginStatus(
    @SerialName("info")
    val info: LoginUserInfo = LoginUserInfo(),
    @SerialName("cookies")
    val cookies: List<String> = emptyList(),
)