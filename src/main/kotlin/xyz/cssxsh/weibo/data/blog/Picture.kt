package xyz.cssxsh.weibo.data.blog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Picture(
    @SerialName("cut_type")
    val cutType: Int,
    @SerialName("height")
    val height: Int,
    @SerialName("type")
    val type: String? = null,
    @SerialName("url")
    val url: String,
    @SerialName("width")
    val width: Int
)