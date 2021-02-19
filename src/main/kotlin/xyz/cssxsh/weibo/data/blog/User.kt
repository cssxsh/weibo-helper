package xyz.cssxsh.weibo.data.blog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("avatar_hd")
    val avatarHighDefinition: String,
    @SerialName("avatar_large")
    val avatarLarge: String,
    @SerialName("domain")
    val domain: String = "",
    @SerialName("follow_me")
    val followMe: Boolean,
    @SerialName("following")
    val following: Boolean,
    @SerialName("id")
    val id: Long,
    @SerialName("idstr")
    val idString: String,
    @SerialName("mbrank")
    val microBlogRank: Int,
    @SerialName("mbtype")
    val microBlogType: Int,
    @SerialName("pc_new")
    val pcNew: Int,
    @SerialName("profile_image_url")
    val profileImageUrl: String,
    @SerialName("profile_url")
    val profileUrl: String,
    @SerialName("screen_name")
    val screenName: String,
    @SerialName("verified")
    val verified: Boolean,
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
    val verifiedTypeExtend : Int? = null,
    @SerialName("weihao")
    val mid: String = "",
    @SerialName("wenda")
    val wenda: Int = 0
)