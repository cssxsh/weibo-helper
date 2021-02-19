package xyz.cssxsh.weibo.data.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import xyz.cssxsh.weibo.data.*
import java.time.OffsetDateTime

@Serializable
data class User(
    @SerialName("allow_all_act_msg")
    val allowAllActionMessage: Boolean,
    @SerialName("allow_all_comment")
    val allowAllComment: Boolean,
    @SerialName("avatar_hd")
    val avatarHighDefinition: String,
    @SerialName("avatar_large")
    val avatarLarge: String,
    @SerialName("block_app")
    val blockApp: Int,
    @SerialName("block_word")
    val blockWord: Int,
    @SerialName("city")
    val city: Int,
    @SerialName("class")
    val classValue: Int,
    @SerialName("cover_image_phone")
    val coverImagePhone: String,
    @SerialName("created_at")
    @Serializable(OffsetDateTimeSerializer::class)
    val createdAt: OffsetDateTime,
    @SerialName("credit_score")
    val creditScore: Int,
    @SerialName("description")
    val description: String,
    @SerialName("domain")
    val domain: String,
    @SerialName("favourites_count")
    val favouritesCount: Int,
    @SerialName("followers_count")
    val followersCount: Int,
    @SerialName("follow_me")
    val followMe: Boolean,
    @SerialName("following")
    val following: Boolean,
    @SerialName("friends_count")
    val friendsCount: Int,
    @SerialName("gender")
    val gender: GenderType,
    @SerialName("geo_enabled")
    val geoEnabled: Boolean,
    @SerialName("has_service_tel")
    val hasServiceTel: Boolean = false,
    @SerialName("id")
    val id: Long,
    @SerialName("idstr")
    val idString: String,
    @SerialName("insecurity")
    val insecurity: Map<String, Boolean>,
    @SerialName("is_guardian")
    val isGuardian: Int = 0,
    @SerialName("is_star")
    val isStar: Int = 0,
    @SerialName("is_teenager")
    val isTeenager: Int = 0,
    @SerialName("is_teenager_list")
    val isTeenagerList: Int = 0,
    @SerialName("lang")
    val lang: String,
    @SerialName("like")
    val like: Boolean,
    @SerialName("like_me")
    val likeMe: Boolean,
    @SerialName("live_status")
    val liveStatus: Int,
    @SerialName("location")
    val location: String,
    @SerialName("mbrank")
    val microBlogRank: Int,
    @SerialName("mbtype")
    val microBlogType: Int,
    @SerialName("weihao")
    val mid: String = "",
    @SerialName("bi_followers_count")
    val mutualFollowersCount: Int,
    @SerialName("name")
    val name: String,
    @SerialName("online_status")
    val onlineStatus: Int,
    @SerialName("pagefriends_count")
    val pageFriendsCount: Int,
    @SerialName("pc_new")
    val pcNew: Int,
    @SerialName("planet_video")
    val planetVideo: Int,
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
    val screenName: String,
    @SerialName("special_follow")
    val specialFollow: Boolean,
    @SerialName("star")
    val star: Int,
    @SerialName("status")
    val status: JsonObject,
    @SerialName("statuses_count")
    val statusesCount: Int,
    @SerialName("story_read_state")
    val storyReadState: Int,
    @SerialName("top_user")
    val topUser: Int = 0,
    @SerialName("url")
    val url: String,
    @SerialName("user_ability")
    val userAbility: Int = 0,
    @SerialName("user_ability_extend")
    val userAbilityExtend: Int = 0,
    @SerialName("user_type")
    val userType: Int,
    @SerialName("urank")
    val userRank: Int,
    @SerialName("verified")
    val verified: Boolean,
    @SerialName("vclub_member")
    val verifiedClubMember: Int,
    @SerialName("verified_contact_email")
    val verifiedContactEmail: String = "",
    @SerialName("verified_contact_mobile")
    val verifiedContactMobile: String = "",
    @SerialName("verified_contact_name")
    val verifiedContactName: String = "",
    @SerialName("verified_detail")
    val verifiedDetail: JsonObject = JsonObject(emptyMap()),
    @SerialName("verified_level")
    val verifiedLevel: Int = 0,
    @SerialName("verified_reason")
    val verifiedReason: String = "",
    @SerialName("verified_reason_modified")
    val verifiedReasonModified: String = "",
    @SerialName("verified_reason_url")
    val verifiedReasonUrl: String = "",
    @SerialName("verified_source")
    val verifiedSource: String = "",
    @SerialName("verified_source_url")
    val verifiedSourceUrl: String = "",
    @SerialName("verified_state")
    val verifiedState: Int? = null,
    @SerialName("verified_trade")
    val verifiedTrade: String,
    @SerialName("verified_type")
    /**
     * -1  普通用户;
     * 0   名人,
     * 1   政府,
     * 2   企业,
     * 3   媒体,
     * 4   校园,
     * 5   网站,
     * 6   应用,
     * 7   团体（机构）,
     * 8   待审企业,
     * 200 初级达人,
     * 220 中高级达人,
     * 400 已故V用户。
     */
    val verifiedType: Int = -1,
    @SerialName("verified_type_ext")
    val verifiedTypeExtend: Int? = null,
    @SerialName("video_mark")
    val videoMark: Int,
    @SerialName("video_play_count")
    val videoPlayCount: Int,
    @SerialName("video_status_count")
    val videoStatusCount: Int,
    @SerialName("wenda")
    val wenda: Int = 0
)