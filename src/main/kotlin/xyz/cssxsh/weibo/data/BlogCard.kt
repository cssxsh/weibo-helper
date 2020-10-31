package xyz.cssxsh.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * cardType = 9
 */
@Serializable
data class BlogCard(
    @SerialName("card_type")
    val cardType: Int,
    @SerialName("itemid")
    val itemId: String,
    @SerialName("mblog")
    val mBlog: JsonElement,
    @SerialName("scheme")
    val scheme: String,
    @SerialName("show_type")
    val showType: Int
)