package xyz.cssxsh.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import xyz.cssxsh.weibo.data.blog.*
import java.time.OffsetDateTime

@Serializable
data class SimpleMicroBlog(
    @SerialName("attitudes_count")
    val attitudesCount: Int = 0,
    @SerialName("attitudes_status")
    val attitudesStatus: Int? = null,
    @SerialName("can_edit")
    val canEdit: Boolean = false,
    @SerialName("comment_manage_info")
    val commentManageInfo: Map<String, Int> = emptyMap(),
    @SerialName("comments_count")
    val commentsCount: Int = 0,
    @SerialName("content_auth")
    val contentAuth: Int? = null,
    @SerialName("created_at")
    @Serializable(OffsetDateTimeSerializer::class)
    val createdAt: OffsetDateTime,
    @SerialName("edit_count")
    val editCount: Int = 0,
    @SerialName("favorited")
    val favorited: Boolean = false,
    @SerialName("geo")
    val geo: JsonElement? = null,
    @SerialName("id")
    val id: Long,
    @SerialName("idstr")
    val idString: String,
    @SerialName("isLongText")
    val isLongText: Boolean = false,
    @SerialName("is_paid")
    val isPaid: Boolean = false,
    @SerialName("is_show_bulletin")
    val isShowBulletin: Int = 0,
    @SerialName("isTop")
    val isTop: Int = 0,
    @SerialName("mlevel")
    val level: Int? = null,
    @SerialName("mark")
    val mark: String? = null,
    @SerialName("mblog_vip_type")
    val microBlogVipType: Int? = null,
    @SerialName("mblogid")
    val microBlogId: String,
    @SerialName("mblogtype")
    val microBlogType: Int? = null,
    @SerialName("mid")
    val mid: String,
    @SerialName("number_display_strategy")
    val numberDisplayStrategy: JsonObject? = null,
    @SerialName("pic_focus_point")
    val pictureFocusPoint: List<JsonObject> = emptyList(),
    @SerialName("pic_ids")
    val pictureIds: List<String> = emptyList(),
    @SerialName("pic_infos")
    val pictureInfos: Map<String, PictureInfo> = emptyMap(),
    @SerialName("pic_num")
    val pictureNumber: Int = 0,
    @SerialName("rcList")
    val rcList: JsonArray,
    @SerialName("repost_type")
    val repostType: Int? = null,
    @SerialName("reposts_count")
    val repostsCount: Int = 0,
    @SerialName("retweeted_status")
    val retweeted: SimpleMicroBlog? = null,
    @SerialName("share_repost_type")
    val shareRepostType: Int? = null,
    @SerialName("showFeedComment")
    val showFeedComment: Boolean = false,
    @SerialName("showFeedRepost")
    val showFeedRepost: Boolean = false,
    @SerialName("source")
    val source: String? = null,
    @SerialName("text")
    val text: String = "",
    @SerialName("textLength")
    val textLength: Int = 0,
    @SerialName("text_raw")
    val textRaw: String? = null,
    @SerialName("title")
    val title: JsonObject? = null,
    @SerialName("topic_struct")
    val topicStruct: List<JsonObject> = emptyList(),
    @SerialName("url_struct")
    val urlStruct: List<JsonObject> = emptyList(),
    @SerialName("user")
    val user: User? = null,
    @SerialName("visible")
    val visible: Map<String, Int> = emptyMap(),
    @SerialName("ok")
    val ok: Int = 1
)
