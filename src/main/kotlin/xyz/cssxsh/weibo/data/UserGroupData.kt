package xyz.cssxsh.weibo.data

import kotlinx.serialization.*

@Serializable
data class UserGroupData(
    @SerialName("feed_default")
    @Serializable(NumberToBooleanSerializer::class)
    val feedDefault: Boolean,
    @SerialName("fetch_hot")
    @Serializable(NumberToBooleanSerializer::class)
    val fetchHot: Boolean,
    @SerialName("groups")
    val groups: List<UserTypeGroup>,
    @SerialName("is_new_segment")
    @Serializable(NumberToBooleanSerializer::class)
    val isNewSegment: Boolean,
    @SerialName("total_number")
    val total: Int
)

@Serializable
data class UserTypeGroup(
    @SerialName("group")
    val list: List<UserGroup>,
    @SerialName("group_type")
    val type: Int,
    @SerialName("priority")
    @Serializable(NumberToBooleanSerializer::class)
    val priority: Boolean = false,
    @SerialName("title")
    val title: String
)

@Serializable
data class UserGroup(
    @SerialName("count")
    val count: Int,
    @SerialName("frequency")
    @Serializable(NumberToBooleanSerializer::class)
    val frequency: Boolean,
    @SerialName("gid")
    val gid: Long,
    @SerialName("is_unread")
    @Serializable(NumberToBooleanSerializer::class)
    val isUnread: Boolean = false,
    @SerialName("title")
    val title: String,
    @SerialName("type")
    val type: UserGroupType,
    @SerialName("uid")
    val uid: Long,
)

@Serializable
data class UserMention(
    @SerialName("statuses")
    val statuses: List<MicroBlog> = emptyList(),
    @SerialName("total_number")
    val total: Int
)

@Serializable
data class UserGroupMembers(
    @SerialName("id")
    val id: Long? = null,
    @SerialName("total_number")
    val total: Int,
    @SerialName("name")
    val name: String? = null,
    @SerialName("users")
    val users: List<UserInfo>
)