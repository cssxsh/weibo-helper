package xyz.cssxsh.weibo.data.blog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.cssxsh.weibo.data.*

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
    @SerialName("wenda")
    private val faqCount: Int = 0,
    @SerialName("idstr")
    private val idString: String,
    @SerialName("is_controlled_by_server")
    @Serializable(NumberToBooleanSerializer::class)
    private val isControlledByServer: Boolean = false,
    @SerialName("location_rights")
    private val locationRights: Int? = null,
    @SerialName("pc_new")
    @Serializable(NumberToBooleanSerializer::class)
    private val pcNew: Boolean,
    @SerialName("planet_video")
    private val planetVideo: Boolean = false
)