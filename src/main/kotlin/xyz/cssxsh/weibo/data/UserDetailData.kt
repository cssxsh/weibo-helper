package xyz.cssxsh.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.util.*

@Serializable
data class UserDetail(
    @SerialName("birthday")
    val birthday: String,
    @SerialName("created_at")
    @Serializable(WeiboDateTimeSerializer::class)
    val created: OffsetDateTime,
    @SerialName("description")
    val description: String,
    @SerialName("followers")
    val followers: UserFollowers,
    @SerialName("gender")
    val gender: GenderType,
    @SerialName("location")
    val location: String,
    @SerialName("desc_text")
    val verifiedText: String,
    @SerialName("verified_url")
    val verifiedUrl: String,
)

@Serializable
data class UserFollower(
    @SerialName("avatar_large")
    val avatar: String,
    @SerialName("id")
    val id: Long,
    @SerialName("screen_name")
    val screen: String
)

@Serializable
data class UserFollowers(
    @SerialName("total_number")
    val total: Int,
    @SerialName("users")
    val users: List<UserFollower>
)

@Serializable
data class UserInfoData(
    @SerialName("tabList")
    val tabs: List<UserTab> = emptyList(),
    @SerialName("user")
    val user: UserInfo
)

interface UserBaseInfo {
    val avatarHighDefinition: String
    val avatarLarge: String
    val id: Long
    val screen: String
    val following: Boolean
}

@Serializable
data class UserInfo(
    @SerialName("avatar_hd")
    override val avatarHighDefinition: String,
    @SerialName("avatar_large")
    override val avatarLarge: String,
    @SerialName("city")
    val city: Int,
    @SerialName("created_at")
    @Serializable(WeiboDateTimeSerializer::class)
    val created: OffsetDateTime,
    @SerialName("description")
    val description: String,
    @SerialName("favourites_count")
    val favouritesCount: Int,
    @SerialName("followers_count")
    val followersCount: Int,
    @SerialName("following")
    override val following: Boolean,
    @SerialName("follow_me")
    val followMe: Boolean,
    @SerialName("friends_count")
    val friendsCount: Int,
    @SerialName("gender")
    val gender: GenderType = GenderType.NONE,
    @SerialName("id")
    override val id: Long,
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
    @SerialName("remark")
    val remark: String,
    @SerialName("screen_name")
    override val screen: String,
    @SerialName("special_follow")
    val specialFollow: Boolean,
//    @SerialName("status") 目前没什么卵用
//    val status: MicroBlog? = null,
    @SerialName("statuses_count")
    val statusesCount: Int,
    @SerialName("url")
    val url: String,
    @SerialName("verified")
    val verified: Boolean,
    @SerialName("verified_type")
    val verifiedType: VerifiedType = VerifiedType.NONE,
): UserBaseInfo

@Serializable
data class UserTab(
    @SerialName("name")
    val name: String,
    @SerialName("tabName")
    val tab: String
)