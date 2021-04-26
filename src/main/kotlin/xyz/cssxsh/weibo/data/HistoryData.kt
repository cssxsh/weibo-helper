package xyz.cssxsh.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HistoryData(
    @SerialName("data")
    val `data`: Map<Int, List<Int>> = emptyMap(),
    @SerialName("ok")
    val ok: Int
)