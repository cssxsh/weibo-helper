package xyz.cssxsh.weibo.data.feed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import xyz.cssxsh.weibo.data.NumberToBooleanSerializer

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
    @SerialName("fid")
    private val fid: String? = null,
    @SerialName("containerid")
    private val containerId: String? = null,
    @SerialName("apipath")
    private val apiPath: String? = null,
    @SerialName("can_edit")
    @Serializable(NumberToBooleanSerializer::class)
    private val canEdit: Boolean = false,
    @SerialName("ad_scene")
    @Serializable(NumberToBooleanSerializer::class)
    private val advertisementScene: Boolean,
    @SerialName("settings")
    private val settings: JsonObject? = null,
    @SerialName("sysgroup")
    private val systemGroup: Int,
    @SerialName("navigation_title")
    private val navigationTitle: String? = null,
    @SerialName("open_scheme")
    private val openScheme: Boolean = false,
)