package xyz.cssxsh.weibo.data.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import xyz.cssxsh.weibo.data.*
import java.time.OffsetDateTime
import java.util.*

@Serializable
data class UserInfo(
    @SerialName("avatar_hd")
    val avatarHighDefinition: String,
    @SerialName("avatar_large")
    val avatarLarge: String,
    @SerialName("city")
    val city: Int,
    @SerialName("created_at")
    @Serializable(WeiboDateTimeSerializer::class)
    val createdAt: OffsetDateTime,
    @SerialName("description")
    val description: String,
    @SerialName("domain")
    val domain: String = "",
    @SerialName("favourites_count")
    val favouritesCount: Int,
    @SerialName("followers_count")
    val followersCount: Int,
    @SerialName("following")
    val following: Boolean,
    @SerialName("follow_me")
    val followMe: Boolean,
    @SerialName("friends_count")
    val friendsCount: Int,
    @SerialName("gender")
    val gender: GenderType = GenderType.NONE,
    @SerialName("id")
    val id: Long,
    @SerialName("lang")
    @Serializable(LocaleSerializer::class)
    val lang: Locale,
    @SerialName("like")
    val like: Boolean,
    @SerialName("like_me")
    val likeMe: Boolean,
    @SerialName("live_status")
    @Serializable(NumberToBooleanSerializer::class)
    val liveStatus: Boolean,
    @SerialName("location")
    val location: String,
    @SerialName("mbrank")
    val microBlogRank: Int,
    @SerialName("mbtype")
    val microBlogType: Int,
    @SerialName("weihao")
    val microNumber: String = "",
    @SerialName("bi_followers_count")
    val mutualFollowersCount: Int,
    @SerialName("name")
    val name: String,
    @SerialName("online_status")
    @Serializable(NumberToBooleanSerializer::class)
    val onlineStatus: Boolean,
    @SerialName("profile_image_url")
    val profileImageUrl: String,
    @SerialName("profile_url")
    val profileUrl: String,
    @SerialName("ptype")
    val profileType: Int,
    @SerialName("province")
    val province: Int,
    @SerialName("remark")
    val remark: String,
    @SerialName("screen_name")
    val screen: String,
    @SerialName("special_follow")
    val specialFollow: Boolean,
    @SerialName("status")
    val status: SimpleMicroBlog? = null,
    @SerialName("statuses_count")
    val statusesCount: Int,
    @SerialName("svip")
    @Serializable(NumberToBooleanSerializer::class)
    val svip: Boolean,
    @SerialName("url")
    val url: String,
    @SerialName("user_ability")
    val userAbility: Int? = null,
    @SerialName("user_ability_extend")
    val userAbilityExtend: Int? = null,
    @SerialName("user_type")
    val userType: Int,
    @SerialName("urank")
    val userRank: Int,
    @SerialName("verified")
    val verified: Boolean,
    @SerialName("verified_type")
    val verifiedType: VerifiedType = VerifiedType.NONE,
    /**
     * TODO VerifiedTypeExtend
     */
    @SerialName("verified_type_ext")
    val verifiedTypeExtend: Int? = null,
    @SerialName("ability_tags")
    private val abilityTags: String? = null,
    @SerialName("allow_all_act_msg")
    private val allowAllActionMessage: Boolean,
    @SerialName("allow_all_comment")
    private val allowAllComment: Boolean,
    @SerialName("attitude_style")
    private val attitudeStyle: String? = null,
    @SerialName("avatargj_id")
    private val avatarGjId: String? = null,
    @SerialName("block_app")
    private val blockApp: Int,
    @SerialName("block_word")
    private val blockWord: Int,
    @SerialName("cardid")
    private val cardId: String? = null,
    @SerialName("cardid_secret")
    private val cardIdSecret: String? = null,
    @SerialName("class")
    private val classValue: Int,
    @SerialName("cover_image")
    private val coverImage: String? = null,
    @SerialName("cover_image_phone")
    private val coverImagePhone: String? = null,
    @SerialName("credit_score")
    private val creditScore: Int,
    @SerialName("dianping")
    private val dianping: String? = null,
    @SerialName("wenda")
    private val faqCount: Int = 0,
    @SerialName("geo_enabled")
    private val geoEnabled: Boolean,
    @SerialName("has_service_tel")
    private val hasServiceTelephone: Boolean = false,
    @SerialName("idstr")
    private val idString: String,
    @SerialName("insecurity")
    private val insecurity: Map<String, Boolean>,
    @SerialName("is_guardian")
    @Serializable(NumberToBooleanSerializer::class)
    private val isGuardian: Boolean = false,
    @SerialName("is_star")
    @Serializable(NumberToBooleanSerializer::class)
    private val isStar: Boolean = false,
    @SerialName("is_teenager")
    @Serializable(NumberToBooleanSerializer::class)
    private val isTeenager: Boolean = false,
    @SerialName("is_teenager_list")
    @Serializable(NumberToBooleanSerializer::class)
    private val isTeenagerList: Boolean = false,
    @SerialName("location_rights")
    @Serializable(NumberToBooleanSerializer::class)
    private val locationRights: Boolean = false,
    @SerialName("pagefriends_count")
    private val pageFriendsCount: Int,
    @SerialName("pay_date")
    private val payDate: String? = null,
    @SerialName("pay_remind")
    @Serializable(NumberToBooleanSerializer::class)
    private val payRemind: Boolean = false,
    @SerialName("pc_new")
    @Serializable(NumberToBooleanSerializer::class)
    private val pcNew: Boolean,
    @SerialName("planet_video")
    @Serializable(NumberToBooleanSerializer::class)
    private val planetVideo: Boolean,
    @SerialName("star")
    private val star: Int,
    @SerialName("story_read_state")
    private val storyReadState: Int,
    @SerialName("top_user")
    @Serializable(NumberToBooleanSerializer::class)
    private val top: Boolean = false,
    @SerialName("vclub_member")
    private val verifiedClubMember: Int,
    @SerialName("verified_contact_email")
    private val verifiedContactEmail: String = "",
    @SerialName("verified_contact_mobile")
    private val verifiedContactMobile: String = "",
    @SerialName("verified_contact_name")
    private val verifiedContactName: String = "",
    @SerialName("verified_detail")
    private val verifiedDetail: JsonObject = JsonObject(emptyMap()),
    @SerialName("verified_level")
    private val verifiedLevel: Int = 0,
    @SerialName("verified_reason")
    private val verifiedReason: String = "",
    @SerialName("verified_reason_modified")
    private val verifiedReasonModified: String = "",
    @SerialName("verified_reason_url")
    private val verifiedReasonUrl: String = "",
    @SerialName("verified_source")
    private val verifiedSource: String = "",
    @SerialName("verified_source_url")
    private val verifiedSourceUrl: String = "",
    @SerialName("verified_state")
    private val verifiedState: Int = 0,
    @SerialName("verified_trade")
    private val verifiedTrade: String,
    @SerialName("video_mark")
    private val videoMark: Int,
    @SerialName("video_play_count")
    private val videoPlayCount: Int,
    @SerialName("video_status_count")
    private val videoStatusCount: Int
)