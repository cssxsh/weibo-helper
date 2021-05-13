package xyz.cssxsh.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class LongTextData(
    @SerialName("data")
    val `data`: LongTextContent? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("http_code")
    val httpCode: Int,
    @SerialName("ok")
    @Serializable(NumberToBooleanSerializer::class)
    val ok: Boolean = true
)

@Serializable
data class LongTextContent(
    @SerialName("longTextContent")
    val content: String? = null,
    @SerialName("topic_struct")
    private val topicStruct: List<JsonObject> = emptyList(),
    @SerialName("url_struct")
    private val urlStruct: List<JsonObject> = emptyList()
)