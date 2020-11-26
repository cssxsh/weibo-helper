package xyz.cssxsh.weibo.data.blog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TempBlog(
    @SerialName("id")
    override val id: String,
    @SerialName("mblogtype")
    override val mBlogType: Int,
    @SerialName("user")
    override val user: User,
    @SerialName("raw_text")
    override val rawText: String,
    @SerialName("pics")
    override val pics: List<Pic> = emptyList(),
    @SerialName("created_at")
    override val createdAt: String
): Blog