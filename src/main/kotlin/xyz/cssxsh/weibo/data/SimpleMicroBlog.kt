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
    val retweeted: SimpleMicroBlog? = null,
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
    @SerialName("actionlog")
    private val actionLog: List<JsonObject> = emptyList(),
    @SerialName("ad_state")
    private val adState: Int? = null,
    @SerialName("annotations")
    private val annotations: List<JsonObject> = emptyList(),
    @SerialName("appid")
    private val appId: Int? = null,
    @SerialName("attitudes_status")
    private val attitudesStatus: Int? = null,
    @SerialName("biz_feature")
    private val bizFeature: Long? = null,
    @SerialName("biz_ids")
    private val bizIds: List<Long> = emptyList(),
    @SerialName("buttons")
    private val buttons: List<JsonObject> = emptyList(),
    @SerialName("can_edit")
    private val canEdit: Boolean = false,
    @SerialName("cardid")
    private val cardId: String? = null,
    @SerialName("comment_manage_info")
    private val commentManageInfo: Map<String, Int> = emptyMap(),
    @SerialName("complaint")
    private val complaint: JsonObject? = null,
    @SerialName("content_auth")
    @Serializable(NumberToBooleanSerializer::class)
    private val contentAuth: Boolean = false,
    @SerialName("dianping")
    private val dianping: String? = null,
    @SerialName("expire_time")
    private val expireTime: Long? = null,
    @SerialName("extend_info")
    private val extendInfo: JsonObject? = null,
    @SerialName("fid")
    private val fid: Long? = null,
    @SerialName("filter_id")
    private val filterId: Long? = null,
    @SerialName("followBtnCode")
    private val followButtonCode: JsonObject? = null,
    @SerialName("geo")
    private val geo: JsonElement? = null, // TODO
    @SerialName("gif_ids")
    private val gifs: String = "",
    @SerialName("hasActionTypeCard")
    @Serializable(NumberToBooleanSerializer::class)
    private val hasActionTypeCard: Boolean = false,
    @SerialName("hide_flag")
    @Serializable(NumberToBooleanSerializer::class)
    private val hideFlag: Boolean = false,
    @SerialName("hide_from_prefix")
    @Serializable(NumberToBooleanSerializer::class)
    private val hideFromPrefix: Boolean = false,
    @SerialName("idstr")
    private val idString: String,
    @SerialName("in_reply_to_screen_name")
    private val inReplyToScreenName: String = "",
    @SerialName("in_reply_to_status_id")
    private val inReplyToStatusId: String = "",
    @SerialName("in_reply_to_user_id")
    private val inReplyToUserId: String = "",
    @SerialName("is_controlled_by_server")
    @Serializable(NumberToBooleanSerializer::class)
    private val isControlledByServer: Boolean = false,
    @SerialName("is_imported_topic")
    private val isImportedTopic: Boolean = false,
    @SerialName("is_paid")
    private val isPaid: Boolean = false,
    @SerialName("is_show_bulletin")
    @Serializable(NumberToBooleanSerializer::class)
    private val isShowBulletin: Boolean = false,
    @SerialName("isTop")
    @Serializable(NumberToBooleanSerializer::class)
    private val isTop: Boolean = false,
    @SerialName("is_vote")
    @Serializable(NumberToBooleanSerializer::class)
    private val isVote: Boolean = false,
    @SerialName("mlevel")
    private val level: Int? = null,
    @SerialName("location_rights")
    @Serializable(NumberToBooleanSerializer::class)
    private val locationRights: Boolean = false,
    @SerialName("mark")
    private val mark: String? = null,
    @SerialName("mblog_menus_new")
    private val microBlogMenusNew: JsonElement? = null,
    @SerialName("mblogid")
    private val microBlogId: String? = null,
    @SerialName("mblog_vip_type")
    private val microBlogVipType: Int? = null,
    @SerialName("mblogtype")
    private val microBlogType: Int? = null,
    @SerialName("more_info_type")
    private val moreInfoType: Int = 0,
    @SerialName("number_display_strategy")
    private val numberDisplayStrategy:  JsonElement? = null,
    @SerialName("ok")
    @Serializable(NumberToBooleanSerializer::class)
    private val ok: Boolean = true,
    @SerialName("ori_mid")
    private val originalMicroBlogId: Long? = null,
    @SerialName("ori_uid")
    private val originalUserId: Long? = null,
    @SerialName("page_info")
    private val pageInfo: JsonObject? = null,
    @SerialName("page_type")
    private val pageType: Int? = null,
    @SerialName("pay_date")
    private val payDate: String? = null,
    @SerialName("pay_remind")
    @Serializable(NumberToBooleanSerializer::class)
    private val payRemind: Boolean = false,
    @SerialName("pending_approval_count")
    private val pendingApprovalCount: Int = 0,
    @SerialName("pic_bg_new")
    private val pictureBackgroundNew: String? = null,
    @SerialName("pic_focus_point")
    private val pictureFocusPoint: List<JsonObject> = emptyList(),
    @SerialName("pic_ids")
    private val pictureIds: List<String> = emptyList(),
    @SerialName("bmiddle_pic")
    private val pictureMiddle: String? = null,
    @SerialName("pic_num")
    private val pictureNumber: Int = 0,
    @SerialName("original_pic")
    private val pictureOriginal: String? = null,
    @SerialName("thumbnail_pic")
    private val pictureThumbnail: String? = null,
    @SerialName("positive_recom_flag")
    private val positiveRecommendFlag: Int? = null,
    @SerialName("rcList")
    private val rcList: JsonArray = JsonArray(emptyList()),
    @SerialName("repost_type")
    private val repostType: Int? = null,
    @SerialName("reward_exhibition_type")
    private val rewardExhibitionType: Int? = null,
    @SerialName("rid")
    private val rid: String? = null,
    @SerialName("safe_tags")
    @Serializable(NumberToBooleanSerializer::class)
    private val safeTags: Boolean = false,
    @SerialName("shield_strategy_type")
    private val shieldStrategyType: Int? = null,
    @SerialName("screen_name_suffix_new")
    private val screenNameSuffixNew: List<JsonObject> = emptyList(),
    @SerialName("show_additional_indication")
    @Serializable(NumberToBooleanSerializer::class)
    private val showAdditionalIndication: Boolean = false,
    @SerialName("showFeedRepost")
    private val showFeedRepost: Boolean = false,
    @SerialName("showFeedComment")
    private val showFeedComment: Boolean = false,
    @SerialName("share_repost_type")
    private val shareRepostType: Int? = null,
    @SerialName("source_allowclick")
    @Serializable(NumberToBooleanSerializer::class)
    private val sourceAllowClick: Boolean = false,
    @SerialName("source_type")
    private val sourceType: Int? = null,
    @SerialName("state")
    private val state: Int? = null,
    @SerialName("sync_mblog")
    private val syncMicroBlog: Boolean = false,
    @SerialName("darwin_tags")
    private val tagsDarwin: JsonArray = JsonArray(emptyList()),
    @SerialName("hot_weibo_tags")
    private val tagsHotBlog: JsonArray = JsonArray(emptyList()),
    @SerialName("pic_tags")
    private val tagsPicture: JsonArray = JsonArray(emptyList()),
    @SerialName("text_tag_tips")
    private val tagsTextTip: JsonArray = JsonArray(emptyList()),
    @SerialName("textLength")
    private val textLength: Int = 0,
    @SerialName("title")
    private val title: JsonObject? = null,
    @SerialName("title_source")
    private val title_source: JsonObject? = null,
    @SerialName("topic_id")
    private val topicId: String? = null,
    @SerialName("topic_struct")
    private val topicStruct: List<JsonObject> = emptyList(),
    @SerialName("url_struct")
    private val urlStruct: List<JsonObject> = emptyList(),
    @SerialName("version")
    private val version: Double? = null,
    @SerialName("visible")
    private val visible: Map<String, Int> = emptyMap()
)
