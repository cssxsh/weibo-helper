package xyz.cssxsh.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class LongTextData(
    @SerialName("data")
    val `data`: Data? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("http_code")
    val httpCode: Int,
    @SerialName("ok")
    val ok: Int
) {
    @Serializable
    data class Data(
        @SerialName("longTextContent")
        val longTextContent: String? = null,
        @SerialName("topic_struct")
        val topicStruct: List<JsonObject> = emptyList(),
        @SerialName("url_struct")
        val urlStruct: List<JsonObject> = emptyList()
    )
}