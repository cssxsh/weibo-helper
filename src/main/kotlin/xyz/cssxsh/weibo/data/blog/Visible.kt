package xyz.cssxsh.weibo.data.blog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Visible(
    @SerialName("list_id")
    val listId: Int,
    @SerialName("type")
    val type: Int
)
