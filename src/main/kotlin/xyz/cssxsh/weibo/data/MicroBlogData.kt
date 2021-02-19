package xyz.cssxsh.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray

@Serializable
data class MicroBlogData(
    @SerialName("data")
    val `data`: Data? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("ok")
    val ok: Int
) {
    @Serializable
    data class Data(
        @SerialName("list")
        val list: List<SimpleMicroBlog>,
        @SerialName("status_visible")
        val statusVisible: Int,
        @SerialName("topicList")
        val topicList: JsonArray
    )
}