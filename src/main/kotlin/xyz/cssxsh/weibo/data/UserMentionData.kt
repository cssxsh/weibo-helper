package xyz.cssxsh.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class UserMentionData(
    @SerialName("data")
    val `data`: UserMention?,
    @SerialName("ok")
    val ok: Int
) {
    @Serializable
    data class UserMention(
        @SerialName("miss_ids")
        val missIds: List<Long>,
        @SerialName("statuses")
        val statuses: List<SimpleMicroBlog> = emptyList(),
        @SerialName("total_number")
        val total: Int,
        @SerialName("extend_card")
        @Serializable(NumberToBooleanSerializer::class)
        private val extendCard: Boolean,
        @SerialName("hasvisible")
        private val hasVisible: Boolean,
        @SerialName("interval")
        private val interval: Int,
        @SerialName("log_ext")
        private val logExtend: JsonObject,
        @SerialName("next_cursor")
        private val nextCursor: Int,
        @SerialName("previous_cursor")
        private val previousCursor: Int,
        @SerialName("request_id")
        private val requestId: String,
        @SerialName("tips_show")
        @Serializable(NumberToBooleanSerializer::class)
        private val tipsShow: Boolean
    )
}