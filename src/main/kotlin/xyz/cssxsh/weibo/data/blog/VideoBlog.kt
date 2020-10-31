package xyz.cssxsh.weibo.data.blog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * mBlogType = 2
 */
@Serializable
data class VideoBlog(
    @SerialName("ad_state")
    val adState: Int? = null,
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
    val createdAt: String,
    @SerialName("edit_at")
    val editAt: String? = null,
    @SerialName("edit_config")
    val editConfig: EditConfig? = null,
    @SerialName("edit_count")
    val editCount: Int? = null,
    @SerialName("enable_comment_guide")
    val enableCommentGuide: Boolean,
    @SerialName("extern_safe")
    val externSafe: Int,
    @SerialName("expire_time")
    val expireTime: Long? = null,
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
    @SerialName("isTop")
    val isTop: Int,
    @SerialName("number_display_strategy")
    val numberDisplayStrategy: NumberDisplayStrategy? = null,
    @SerialName("mark")
    val mark: String? = null,
    @SerialName("mblog_menu_new_style")
    val mBlogMenuNewStyle: Int,
    @SerialName("mblog_vip_type")
    val mBlogVipType: Int,
    @SerialName("mblogtype")
    val mBlogType: Int,
    @SerialName("mid")
    val mid: String,
    @SerialName("mlevel")
    val mLevel: Int,
    @SerialName("more_info_type")
    val moreInfoType: Int,
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
    @SerialName("pic_types")
    val picTypes: String,
    @SerialName("pics")
    override val pics: List<Pic> = emptyList(),
    @SerialName("promotion_info")
    val promotionInfo: JsonElement? = null,
    @SerialName("raw_text")
    override val rawText: String,
    @SerialName("reposts_count")
    val repostsCount: Int,
    @SerialName("readtimetype")
    val readTimeType: String? = null,
    @SerialName("reward_exhibition_type")
    val rewardExhibitionType: Int,
    @SerialName("reward_scheme")
    val rewardScheme: JsonElement? = null,
    @SerialName("safe_tags")
    val safeTags: Int? = null,
    @SerialName("show_additional_indication")
    val showAdditionalIndication: Int,
    @SerialName("show_attitude_bar")
    val showAttitudeBar: Int,
    @SerialName("source")
    val source: String,
    @SerialName("text")
    val text: String,
    @SerialName("textLength")
    val textLength: Int,
    @SerialName("title")
    val title: Title,
    @SerialName("timestamp_text")
    val timestampText: String? = null,
    @SerialName("thumbnail_pic")
    val thumbnailPic: String? = null,
    @SerialName("user")
    override val user: User,
    @SerialName("version")
    val version: Int? = null,
    @SerialName("visible")
    val visible: Visible,
    @SerialName("weibo_position")
    val weiboPosition: Int
): Blog