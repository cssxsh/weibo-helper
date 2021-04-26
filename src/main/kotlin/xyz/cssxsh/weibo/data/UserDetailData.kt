package xyz.cssxsh.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.cssxsh.weibo.data.profile.UserDetail

@Serializable
data class UserDetailData(
    @SerialName("data")
    val `data`: UserDetail? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("ok")
    @Serializable(NumberToBooleanSerializer::class)
    val ok: Boolean = true
)