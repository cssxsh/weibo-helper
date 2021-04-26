package xyz.cssxsh.weibo.data.blog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class LongTextContent(
    @SerialName("longTextContent")
    val content: String? = null,
    @SerialName("topic_struct")
    private val topicStruct: List<JsonObject> = emptyList(),
    @SerialName("url_struct")
    private val urlStruct: List<JsonObject> = emptyList()
)