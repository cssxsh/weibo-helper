package xyz.cssxsh.weibo.data.blog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Title(
    @SerialName("base_color")
    val baseColor: Int,
    @SerialName("text")
    val text: String
)