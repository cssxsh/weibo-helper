package xyz.cssxsh.weibo.data.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Tab(
    @SerialName("name")
    val name: String,
    @SerialName("tabName")
    val tab: String
)