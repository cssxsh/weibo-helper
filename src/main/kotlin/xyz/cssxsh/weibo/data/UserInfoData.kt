package xyz.cssxsh.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.cssxsh.weibo.data.profile.*

@Serializable
data class UserInfoData(
    @SerialName("data")
    val `data`: Data? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("ok")
    val ok: Int
) {
    @Serializable
    data class Data(
        @SerialName("tabList")
        val tabList: List<Tab>,
        @SerialName("user")
        val user: User
    )
}