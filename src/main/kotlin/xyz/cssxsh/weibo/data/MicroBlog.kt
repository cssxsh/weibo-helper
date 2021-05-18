package xyz.cssxsh.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.time.OffsetDateTime

@Serializable
data class MicroBlog(
    @SerialName("attitudes_count")
    val attitudesCount: Int = 0,
    @SerialName("comments_count")
    val commentsCount: Int = 0,
    @SerialName("created_at")
    @Serializable(WeiboDateTimeSerializer::class)
    val createdAt: OffsetDateTime,
    @SerialName("continue_tag")
    val continueTag: JsonObject? = null,
    @SerialName("deleted")
    @Serializable(NumberToBooleanSerializer::class)
    val deleted: Boolean = false,
    @SerialName("edit_at")
    @Serializable(WeiboDateTimeSerializer::class)
    val editAt: OffsetDateTime? = null,
    @SerialName("edit_count")
    val editCount: Int = 0,
    @SerialName("favorited")
    val favorited: Boolean = false,
    @SerialName("id")
    val id: Long,
    @SerialName("isLongText")
    val isLongText: Boolean = false,
    @SerialName("mid")
    val mid: String,
    @SerialName("pic_infos")
    val pictureInfos: Map<String, PictureInfo> = emptyMap(),
    @SerialName("picStatus")
    val pictureStatus: String? = null,
    @SerialName("pic_types")
    val pictureTypes: String? = null,
    @SerialName("reposts_count")
    val repostsCount: Int = 0,
    @SerialName("retweeted_status")
    val retweeted: MicroBlog? = null,
    @SerialName("source")
    val source: String = "",
    @SerialName("text")
    val text: String = "",
    @SerialName("text_raw")
    val textRaw: String? = null,
    @SerialName("truncated")
    val truncated: Boolean = false,
    @SerialName("user")
    val user: User? = null,
    @SerialName("userType")
    val userType: Int? = null,
//    @SerialName("actionlog")
//    private val actionLog: List<JsonObject> = emptyList(),
//    @SerialName("ad_state")
//    private val adState: Int? = null,
//    @SerialName("annotations")
//    private val annotations: List<JsonObject> = emptyList(),
//    @SerialName("appid")
//    private val appId: Int? = null,
//    @SerialName("attitudes_status")
//    private val attitudesStatus: Int? = null,
//    @SerialName("biz_feature")
//    private val bizFeature: Long? = null,
//    @SerialName("biz_ids")
//    private val bizIds: List<Long> = emptyList(),
//    @SerialName("buttons")
//    private val buttons: List<JsonObject> = emptyList(),
//    @SerialName("can_edit")
//    private val canEdit: Boolean = false,
//    @SerialName("cardid")
//    private val cardId: String? = null,
//    @SerialName("comment_manage_info")
//    private val commentManageInfo: Map<String, Int> = emptyMap(),
//    @SerialName("complaint")
//    private val complaint: JsonObject? = null,
//    @SerialName("content_auth")
//    @Serializable(NumberToBooleanSerializer::class)
//    private val contentAuth: Boolean = false,
//    @SerialName("dianping")
//    private val dianping: String? = null,
//    @SerialName("expire_time")
//    private val expireTime: Long? = null,
//    @SerialName("extend_info")
//    private val extendInfo: JsonObject? = null,
//    @SerialName("fid")
//    private val fid: Long? = null,
//    @SerialName("filter_id")
//    private val filterId: Long? = null,
//    @SerialName("followBtnCode")
//    private val followButtonCode: JsonObject? = null,
//    @SerialName("geo")
//    private val geo: JsonElement? = null,
//    @SerialName("gif_ids")
//    private val gifs: String = "",
//    @SerialName("hasActionTypeCard")
//    @Serializable(NumberToBooleanSerializer::class)
//    private val hasActionTypeCard: Boolean = false,
//    @SerialName("hide_flag")
//    @Serializable(NumberToBooleanSerializer::class)
//    private val hideFlag: Boolean = false,
//    @SerialName("hide_from_prefix")
//    @Serializable(NumberToBooleanSerializer::class)
//    private val hideFromPrefix: Boolean = false,
//    @SerialName("idstr")
//    private val idString: String,
//    @SerialName("in_reply_to_screen_name")
//    private val inReplyToScreenName: String = "",
//    @SerialName("in_reply_to_status_id")
//    private val inReplyToStatusId: String = "",
//    @SerialName("in_reply_to_user_id")
//    private val inReplyToUserId: String = "",
//    @SerialName("is_controlled_by_server")
//    @Serializable(NumberToBooleanSerializer::class)
//    private val isControlledByServer: Boolean = false,
//    @SerialName("is_imported_topic")
//    private val isImportedTopic: Boolean = false,
//    @SerialName("is_paid")
//    private val isPaid: Boolean = false,
//    @SerialName("is_show_bulletin")
//    @Serializable(NumberToBooleanSerializer::class)
//    private val isShowBulletin: Boolean = false,
//    @SerialName("isTop")
//    @Serializable(NumberToBooleanSerializer::class)
//    private val isTop: Boolean = false,
//    @SerialName("is_vote")
//    @Serializable(NumberToBooleanSerializer::class)
//    private val isVote: Boolean = false,
//    @SerialName("mlevel")
//    private val level: Int? = null,
//    @SerialName("location_rights")
//    @Serializable(NumberToBooleanSerializer::class)
//    private val locationRights: Boolean = false,
//    @SerialName("mark")
//    private val mark: String? = null,
//    @SerialName("mblog_menus_new")
//    private val microBlogMenusNew: JsonElement? = null,
//    @SerialName("mblogid")
//    private val microBlogId: String? = null,
//    @SerialName("mblog_vip_type")
//    private val microBlogVipType: Int? = null,
//    @SerialName("mblogtype")
//    private val microBlogType: Int? = null,
//    @SerialName("more_info_type")
//    private val moreInfoType: Int = 0,
//    @SerialName("number_display_strategy")
//    private val numberDisplayStrategy: JsonElement? = null,
//    @SerialName("ok")
//    @Serializable(NumberToBooleanSerializer::class)
//    private val ok: Boolean = true,
//    @SerialName("ori_mid")
//    private val originalMicroBlogId: Long? = null,
//    @SerialName("ori_uid")
//    private val originalUserId: Long? = null,
//    @SerialName("page_info")
//    private val pageInfo: JsonObject? = null,
//    @SerialName("page_type")
//    private val pageType: Int? = null,
//    @SerialName("pay_date")
//    private val payDate: String? = null,
//    @SerialName("pay_remind")
//    @Serializable(NumberToBooleanSerializer::class)
//    private val payRemind: Boolean = false,
//    @SerialName("pending_approval_count")
//    private val pendingApprovalCount: Int = 0,
//    @SerialName("pic_bg_new")
//    private val pictureBackgroundNew: String? = null,
//    @SerialName("pic_focus_point")
//    private val pictureFocusPoint: List<JsonObject> = emptyList(),
//    @SerialName("pic_ids")
//    private val pictureIds: List<String> = emptyList(),
//    @SerialName("bmiddle_pic")
//    private val pictureMiddle: String? = null,
//    @SerialName("pic_num")
//    private val pictureNumber: Int = 0,
//    @SerialName("original_pic")
//    private val pictureOriginal: String? = null,
//    @SerialName("thumbnail_pic")
//    private val pictureThumbnail: String? = null,
//    @SerialName("positive_recom_flag")
//    private val positiveRecommendFlag: Int? = null,
//    @SerialName("rcList")
//    private val rcList: JsonArray = JsonArray(emptyList()),
//    @SerialName("repost_type")
//    private val repostType: Int? = null,
//    @SerialName("reward_exhibition_type")
//    private val rewardExhibitionType: Int? = null,
//    @SerialName("rid")
//    private val rid: String? = null,
//    @SerialName("safe_tags")
//    @Serializable(NumberToBooleanSerializer::class)
//    private val safeTags: Boolean = false,
//    @SerialName("shield_strategy_type")
//    private val shieldStrategyType: Int? = null,
//    @SerialName("screen_name_suffix_new")
//    private val screenNameSuffixNew: List<JsonObject> = emptyList(),
//    @SerialName("show_additional_indication")
//    @Serializable(NumberToBooleanSerializer::class)
//    private val showAdditionalIndication: Boolean = false,
//    @SerialName("showFeedRepost")
//    private val showFeedRepost: Boolean = false,
//    @SerialName("showFeedComment")
//    private val showFeedComment: Boolean = false,
//    @SerialName("share_repost_type")
//    private val shareRepostType: Int? = null,
//    @SerialName("source_allowclick")
//    @Serializable(NumberToBooleanSerializer::class)
//    private val sourceAllowClick: Boolean = false,
//    @SerialName("source_type")
//    private val sourceType: Int? = null,
//    @SerialName("state")
//    private val state: Int? = null,
//    @SerialName("sync_mblog")
//    private val syncMicroBlog: Boolean = false,
//    @SerialName("darwin_tags")
//    private val tagsDarwin: JsonArray = JsonArray(emptyList()),
//    @SerialName("hot_weibo_tags")
//    private val tagsHotBlog: JsonArray = JsonArray(emptyList()),
//    @SerialName("pic_tags")
//    private val tagsPicture: JsonArray = JsonArray(emptyList()),
//    @SerialName("text_tag_tips")
//    private val tagsTextTip: JsonArray = JsonArray(emptyList()),
//    @SerialName("textLength")
//    private val textLength: Int = 0,
//    @SerialName("title")
//    private val title: JsonObject? = null,
//    @SerialName("title_source")
//    private val title_source: JsonObject? = null,
//    @SerialName("topic_id")
//    private val topicId: String? = null,
//    @SerialName("topic_struct")
//    private val topicStruct: List<JsonObject> = emptyList(),
//    @SerialName("url_struct")
//    private val urlStruct: List<JsonObject> = emptyList(),
//    @SerialName("tag_struct")
//    private val tagStruct: List<JsonObject> = emptyList(),
//    @SerialName("version")
//    private val version: Double? = null,
//    @SerialName("visible")
//    private val visible: Map<String, Int> = emptyMap()
)

@Serializable
data class Picture(
    @SerialName("height")
    val height: Int,
    @SerialName("type")
    val type: String? = null,
    @SerialName("url")
    val url: String,
    @SerialName("width")
    val width: Int,
//    @SerialName("cut_type")
//    private val cutType: Int
)

@Serializable
data class PictureInfo(
    @SerialName("pic_id")
    val id: String,
    @SerialName("bmiddle")
    val middle: Picture,
    @SerialName("large")
    val large: Picture,
    @SerialName("largest")
    val largest: Picture,
    @SerialName("mw2000")
    val mw2000: Picture,
    @SerialName("original")
    val original: Picture,
    @SerialName("thumbnail")
    val thumbnail: Picture,
    @SerialName("blur")
    val blur: Picture? = null,
    /**
     * TODO PictureStatus
     */
    @SerialName("pic_status")
    val status: Int,
    @SerialName("type")
    val type: PictureType,
//    @SerialName("actionlog")
//    private val actionLog: List<JsonObject> = emptyList(),
//    @SerialName("button_name")
//    private val buttonName: String? = null,
//    @SerialName("button_scheme")
//    private val buttonScheme: String? = null,
//    @SerialName("focus_point")
//    private val focusPoint: JsonObject? = null,
//    @SerialName("fid")
//    private val fid: String? = null,
//    @SerialName("filter_id")
//    private val filterId: String? = null,
//    @SerialName("object_id")
//    private val objectId: String,
//    @SerialName("photo_tag")
//    private val photoTag: Int,
//    @SerialName("sticker_id")
//    private val stickerId: String? = null,
//    @SerialName("pic_tags")
//    private val tags: List<JsonObject> = emptyList(),
//    @SerialName("video")
//    private val video: String? = null,
//    @SerialName("video_object_id")
//    private val videoObjectId: String? = null
)

@Serializable
data class User(
    @SerialName("avatar_hd")
    val avatarHighDefinition: String,
    @SerialName("avatar_large")
    val avatarLarge: String,
    @SerialName("domain")
    val domain: String? = null,
    @SerialName("following")
    val following: Boolean,
    @SerialName("follow_me")
    val followMe: Boolean,
    @SerialName("id")
    val id: Long,
    @SerialName("mbrank")
    val microBlogRank: Int,
    @SerialName("mbtype")
    val microBlogType: Int,
    @SerialName("weihao")
    val microNumber: String = "",
    @SerialName("profile_image_url")
    val profileImageUrl: String,
    @SerialName("profile_url")
    val profileUrl: String,
    @SerialName("screen_name")
    val screen: String,
    @SerialName("verified")
    val verified: Boolean,
    @SerialName("verified_type")
    val verifiedType: VerifiedType = VerifiedType.NONE,
    /**
     * TODO VerifiedTypeExtend
     */
    @SerialName("verified_type_ext")
    val verifiedTypeExtend: Int? = null,
//    @SerialName("wenda")
//    private val faqCount: Int = 0,
//    @SerialName("idstr")
//    private val idString: String,
//    @SerialName("is_controlled_by_server")
//    @Serializable(NumberToBooleanSerializer::class)
//    private val isControlledByServer: Boolean = false,
//    @SerialName("location_rights")
//    private val locationRights: Int? = null,
//    @SerialName("pc_new")
//    @Serializable(NumberToBooleanSerializer::class)
//    private val pcNew: Boolean,
//    @SerialName("planet_video")
//    private val planetVideo: Boolean = false
)

@Serializable
data class LongTextContent(
    @SerialName("longTextContent")
    val content: String? = null,
//    @SerialName("topic_struct")
//    private val topicStruct: List<JsonObject> = emptyList(),
//    @SerialName("url_struct")
//    private val urlStruct: List<JsonObject> = emptyList()
)

@Serializable
data class TimelineData(
    @SerialName("max_id")
    val maxId: Long,
    @SerialName("ok")
    @Serializable(NumberToBooleanSerializer::class)
    val ok: Boolean = true,
    @SerialName("since_id")
    val sinceId: Long,
    @SerialName("statuses")
    val statuses: List<MicroBlog> = emptyList(),
//    @SerialName("max_id_str")
//    private val maxIdString: String? = null,
//    @SerialName("since_id_str")
//    private val sinceIdString: String? = null,
//    @SerialName("total_number")
//    private val total: Int? = null,
)

@Serializable
data class UserBlog(
    @SerialName("list")
    val list: List<MicroBlog> = emptyList(),
    @SerialName("status_visible")
    @Serializable(NumberToBooleanSerializer::class)
    val statusVisible: Boolean,
//    @SerialName("bottom_tips_visible")
//    private val bottomTipsVisible: Boolean = false,
//    @SerialName("bottom_tips_text")
//    private val bottomTipsText: String = "",
//    @SerialName("topicList")
//    private val topicList: JsonArray
)
