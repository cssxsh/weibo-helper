package xyz.cssxsh.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray

@Serializable
data class UserBlogData(
    @SerialName("data")
    val `data`: UserBlog? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("ok")
    @Serializable(NumberToBooleanSerializer::class)
    val ok: Boolean = true
) {
    @Serializable
    data class UserBlog(
        @SerialName("list")
        val list: List<SimpleMicroBlog> = emptyList(),
        @SerialName("status_visible")
        @Serializable(NumberToBooleanSerializer::class)
        val statusVisible: Boolean,
        @SerialName("bottom_tips_visible")
        val bottomTipsVisible: Boolean = false,
        @SerialName("bottom_tips_text")
        val bottomTipsText: String = "",
        @SerialName("topicList")
        private val topicList: JsonArray
    )
}