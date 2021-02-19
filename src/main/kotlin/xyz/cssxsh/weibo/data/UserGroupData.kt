package xyz.cssxsh.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class UserGroupData(
    @SerialName("feed_default")
    val feedDefault: Int,
    @SerialName("fetch_hot")
    val fetchHot: Int,
    @SerialName("groups")
    val groups: List<Group>,
    @SerialName("is_new_segment")
    val isNewSegment: Int,
    @SerialName("ok")
    val ok: Int,
    @SerialName("total_number")
    val totalNumber: Int
) {
    @Serializable
    data class Group(
        @SerialName("group")
        val list: List<Item>,
        @SerialName("group_type")
        val type: Int,
        @SerialName("priority")
        val priority: Int = 0,
        @SerialName("title")
        val title: String
    )

    @Serializable
    data class Item(
        @SerialName("ad_scene")
        val advertisementScene: Int,
        @SerialName("apipath")
        val apiPath: String? = null,
        @SerialName("can_edit")
        val canEdit: Int = 0,
        @SerialName("containerid")
        val containerId: String? = null,
        @SerialName("count")
        val count: Int,
        @SerialName("fid")
        val fid: String? = null,
        @SerialName("frequency")
        val frequency: Int,
        @SerialName("gid")
        val gid: Long,
        @SerialName("is_unread")
        val isUnread: Int = 0,
        @SerialName("navigation_title")
        val navigationTitle: String? = null,
        @SerialName("open_scheme")
        val openScheme: Boolean = false,
        @SerialName("settings")
        val settings: JsonObject? = null,
        @SerialName("sysgroup")
        val systemGroup: Int,
        @SerialName("title")
        val title: String,
        @SerialName("type")
        val type: Int,
        @SerialName("uid")
        val uid: Long
    )
}