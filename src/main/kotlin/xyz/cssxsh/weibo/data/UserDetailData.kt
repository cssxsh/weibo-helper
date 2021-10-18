package xyz.cssxsh.weibo.data

import kotlinx.serialization.*
import java.time.*
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
    @SerialName("description")
    val description: String,
    @SerialName("favourites_count")
    val favourites: Int = 0,
    @SerialName("followers_count")
    val followers: Int = 0,
    @SerialName("following")
    override val following: Boolean = false,
    @SerialName("follow_me")
    val followed: Boolean = false,
    @SerialName("friends_count")
    val friends: Int = 0,
    @SerialName("gender")
    val gender: GenderType = GenderType.NONE,
    @SerialName("id")
    override val id: Long,
    @SerialName("lang")
    @Serializable(LocaleSerializer::class)
    val lang: Locale = Locale.CHINA,
    @SerialName("like")
    val liking: Boolean = false,
    @SerialName("like_me")
    val liked: Boolean = false,
    @SerialName("location")
    val location: String,
    @SerialName("bi_followers_count")
    val mutualFollowers: Int = 0,
    @SerialName("profile_image_url")
    val profileImageUrl: String,
    @SerialName("profile_url")
    val profileUrl: String,
    @SerialName("screen_name")
    override val screen: String,
    @SerialName("special_follow")
    val specialFollow: Boolean = false,
    @SerialName("statuses_count")
    val statuses: Int = 0,
    @SerialName("url")
    val url: String,
    @SerialName("verified")
    val verified: Boolean = false,
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