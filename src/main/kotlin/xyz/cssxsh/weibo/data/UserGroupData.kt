package xyz.cssxsh.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.cssxsh.weibo.data.feed.UserGroup

@Serializable
data class UserGroupData(
    @SerialName("feed_default")
    @Serializable(NumberToBooleanSerializer::class)
    val feedDefault: Boolean,
    @SerialName("fetch_hot")
    @Serializable(NumberToBooleanSerializer::class)
    val fetchHot: Boolean,
    @SerialName("groups")
    val groups: List<Group>,
    @SerialName("is_new_segment")
    @Serializable(NumberToBooleanSerializer::class)
    val isNewSegment: Boolean,
    @SerialName("ok")
    @Serializable(NumberToBooleanSerializer::class)
    val ok: Boolean = true,
    @SerialName("total_number")
    val total: Int
) {
    @Serializable
    data class Group(
        @SerialName("group")
        val list: List<UserGroup>,
        @SerialName("group_type")
        val type: Int,// XXX
        @SerialName("priority")
        @Serializable(NumberToBooleanSerializer::class)
        val priority: Boolean = false,
        @SerialName("title")
        val title: String
    )
}