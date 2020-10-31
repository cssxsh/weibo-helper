package xyz.cssxsh.weibo.data.blog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlchemyParams(
    @SerialName("ug_red_envelope")
    val ugRedEnvelope: Boolean
)