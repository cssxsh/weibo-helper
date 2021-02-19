package xyz.cssxsh.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
        val displayName: String,
        @SerialName("uniqueid")
        val uid: Long
    )
}