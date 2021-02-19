package xyz.cssxsh.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimelineData(
    @SerialName("max_id")
    val maxId: String,
    @SerialName("max_id_str")
    val maxIdString: String,
    @SerialName("ok")
    val ok: Int,
    @SerialName("since_id")
    val sinceId: String,
    @SerialName("since_id_str")
    val sinceIdString: String,
    @SerialName("statuses")
    val statuses: List<SimpleMicroBlog>
)