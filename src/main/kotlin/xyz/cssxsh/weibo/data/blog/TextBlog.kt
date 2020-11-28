package xyz.cssxsh.weibo.data.blog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * mBlogType = 0
 */
@Serializable
data class TextBlog(
    @SerialName("alchemy_params")
    val alchemyParams: AlchemyParams,
    @SerialName("attitudes_count")
    val attitudesCount: Int,
    @SerialName("bid")
    val bid: String,
    @SerialName("bmiddle_pic")
    val bMiddlePic: String? = null,
    @SerialName("can_edit")
    val canEdit: Boolean,
    @SerialName("comments_count")
    val commentsCount: Int,
    @SerialName("content_auth")
    val contentAuth: Int,
    @SerialName("created_at")
    override val createdAt: String,
    @SerialName("edit_at")
    val editAt: String? = null,
    @SerialName("edit_config")
    val editConfig: EditConfig? = null,
    @SerialName("edit_count")
    val editCount: Int? = null,
    @SerialName("enable_comment_guide")
    val enableCommentGuide: Boolean? = null,
    @SerialName("extern_safe")
    val externSafe: Int,
    @SerialName("favorited")
    val favorited: Boolean,
    @SerialName("fid")
    val fid: Long? = null,
    @SerialName("hide_flag")
    val hideFlag: Int,
    @SerialName("id")
    override val id: String,
    @SerialName("idstr")
    val idStr: String,
    @SerialName("isLongText")
    val isLongText: Boolean,
    @SerialName("is_paid")
    val isPaid: Boolean,
    @SerialName("is_imported_topic")
    val isImportedTopic: Boolean? = null,
    @SerialName("mark")
    val mark: String? = null,
    @SerialName("mblog_menu_new_style")
    val mBlogMenuNewStyle: Int,
    @SerialName("mblog_vip_type")
    val mBlogVipType: Int,
    @SerialName("mblogtype")
    override val mBlogType: Int,
    @SerialName("mid")
    val mid: String,
    @SerialName("mlevel")
    val mLevel: Int,
    @SerialName("more_info_type")
    val moreInfoType: Int,
    @SerialName("number_display_strategy")
    val numberDisplayStrategy: NumberDisplayStrategy? = null,
    @SerialName("original_pic")
    val originalPic: String? = null,
    @SerialName("obj_ext")
    val objExt: String? = null,
    @SerialName("page_info")
    val pageInfo: JsonElement? = null,
    @SerialName("pending_approval_count")
    val pendingApprovalCount: Int,
    @SerialName("pic_num")
    val picNum: Int,
    @SerialName("picStatus")
    val picStatus: String? = null,
    @SerialName("pic_types")
    val picTypes: String? = null,
    @SerialName("pics")
    override val pics: List<Pic> = emptyList(),
    @SerialName("pid")
    val pid: Long? = null,
    @SerialName("pidstr")
    val pidStr: String? = null,
    @SerialName("raw_text")
    override val rawText: String,
    @SerialName("readtimetype")
    val readTimeType: String? = null,
    @SerialName("reposts_count")
    val repostsCount: Int,
    @SerialName("reward_exhibition_type")
    val rewardExhibitionType: Int,
    @SerialName("reward_scheme")
    val rewardScheme: JsonElement? = null,
    @SerialName("retweeted_status")
    val retweetedStatus: JsonElement? = null,
    @SerialName("repost_type")
    val repostType: Int? = null,
    @SerialName("rid")
    val rid: String? = null,
    @SerialName("safe_tags")
    val safeTags: Int? = null,
    @SerialName("show_additional_indication")
    val showAdditionalIndication: Int,
    @SerialName("show_attitude_bar")
    val showAttitudeBar: Int,
    @SerialName("source")
    val source: String,
    @SerialName("sync_mblog")
    val syncMBlog: Boolean? = null,
    @SerialName("text")
    val text: String,
    @SerialName("textLength")
    val textLength: Int? = null,
    @SerialName("timestamp_text")
    val timestampText: String? = null,
    @SerialName("thumbnail_pic")
    val thumbnailPic: String? = null,
    @SerialName("topic_id")
    val topicId: String? = null,
    @SerialName("user")
    override val user: User,
    @SerialName("visible")
    val visible: Visible,
    @SerialName("version")
    val version: Int? = null,
    @SerialName("weibo_position")
    val weiboPosition: Int,
    @SerialName("attitude_dynamic_adid")
    val attitudeDynamicAdId: String? = null
): Blog