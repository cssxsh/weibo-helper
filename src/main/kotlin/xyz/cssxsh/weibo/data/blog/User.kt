package xyz.cssxsh.weibo.data.blog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("avatar_hd")
    val avatarHd: String,
    @SerialName("badge")
    val badge: Map<String, Int>,
    @SerialName("close_blue_v")
    val closeBlueV: Boolean,
    @SerialName("cover_image_phone")
    val coverImagePhone: String,
    @SerialName("description")
    val description: String,
    @SerialName("follow_count")
    val followCount: Int,
    @SerialName("follow_me")
    val followMe: Boolean,
    @SerialName("followers_count")
    val followersCount: Int,
    @SerialName("following")
    val following: Boolean,
    @SerialName("gender")
    val gender: String,
    @SerialName("id")
    val id: Long,
    @SerialName("like")
    val like: Boolean,
    @SerialName("like_me")
    val likeMe: Boolean,
    @SerialName("mbrank")
    val mbrank: Int,
    @SerialName("mbtype")
    val mbtype: Int,
    @SerialName("profile_image_url")
    val profileImageUrl: String,
    @SerialName("profile_url")
    val profileUrl: String,
    @SerialName("screen_name")
    val screenName: String,
    @SerialName("statuses_count")
    val statusesCount: Int,
    @SerialName("urank")
    val urank: Int,
    @SerialName("verified")
    val verified: Boolean,
    @SerialName("verified_reason")
    val verifiedReason: String,
    @SerialName("verified_type")
    val verifiedType: Int,
    @SerialName("verified_type_ext")
    val verifiedTypeExt: Int
)