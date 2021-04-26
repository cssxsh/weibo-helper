package xyz.cssxsh.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimelineData(
    @SerialName("max_id")
    val maxId: Long,
    @SerialName("ok")
    @Serializable(NumberToBooleanSerializer::class)
    val ok: Boolean = true,
    @SerialName("since_id")
    val sinceId: Long,
    @SerialName("statuses")
    val statuses: List<SimpleMicroBlog> = emptyList(),
    @SerialName("max_id_str")
    private val maxIdString: String? = null,
    @SerialName("since_id_str")
    private val sinceIdString: String? = null,
    @SerialName("total_number")
    private val total: Int? = null,
)