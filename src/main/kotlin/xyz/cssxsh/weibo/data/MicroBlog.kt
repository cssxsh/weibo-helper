package xyz.cssxsh.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.time.OffsetDateTime

val MicroBlog.isLongText get() = (continueTag != null)

@Serializable
data class MicroBlog(
    /**
     * 点赞数
     */
    @SerialName("attitudes_count")
    val attitudes: Int = 0,
    /**
     * 评论数
     */
    @SerialName("comments_count")
    val comments: Int = 0,
    @SerialName("created_at")
    @Serializable(WeiboDateTimeSerializer::class)
    val created: OffsetDateTime,
    @SerialName("continue_tag")
    internal val continueTag: JsonObject? = null,
    @SerialName("favorited")
    val favorited: Boolean = false,
    @SerialName("id")
    val id: Long,
    @SerialName("mid")
    val mid: String,
    @SerialName("pic_ids")
    val pictures: List<String> = emptyList(),
    /**
     * 转发数
     */
    @SerialName("reposts_count")
    val reposts: Int = 0,
    @SerialName("retweeted_status")
    val retweeted: MicroBlog? = null,
    @SerialName("text_raw")
    val raw: String? = null,
    @SerialName("user")
    val user: MicroBlogUser? = null,
    @SerialName("url_struct")
    val urls: List<UrlStruct> = emptyList()
)

@Serializable
data class UrlStruct(
    @SerialName("long_url")
    val long: String,
    @SerialName("short_url")
    val short: String,
    @SerialName("url_title")
    val title: String
)

@Serializable
data class MicroBlogUser(
    @SerialName("avatar_hd")
    override val avatarHighDefinition: String = "",
    @SerialName("avatar_large")
    override val avatarLarge: String ="",
    @SerialName("following")
    override val following: Boolean = false,
    @SerialName("follow_me")
    val followMe: Boolean = false,
    @SerialName("id")
    override val id: Long = 0,
    @SerialName("profile_image_url")
    val profileImageUrl: String = "",
    @SerialName("profile_url")
    val profileUrl: String = "",
    @SerialName("screen_name")
    override val screen: String = "",
    @SerialName("verified")
    val verified: Boolean = false,
    @SerialName("verified_type")
    val verifiedType: VerifiedType = VerifiedType.NONE,
): UserBaseInfo

@Serializable
data class LongTextContent(
    @SerialName("longTextContent")
    val content: String? = null,
)

@Serializable
data class TimelineData(
    @SerialName("max_id")
    val maxId: Long = 0,
    @SerialName("ok")
    @Serializable(NumberToBooleanSerializer::class)
    val ok: Boolean = true,
    @SerialName("since_id")
    val sinceId: Long = 0,
    @SerialName("statuses")
    val statuses: List<MicroBlog> = emptyList(),
)

@Serializable
data class UserBlog(
    @SerialName("list")
    val list: List<MicroBlog> = emptyList()
)

@Serializable
data class EmotionData(
    @SerialName("emoticon")
    val emoticon: EmoticonMap,
//    @SerialName("emoticon_format")
//    val emoticonFormat: List<EmoticonFormat>
)

@Serializable
data class EmoticonMap(
    @SerialName("brand")
    val brand: Map<String, Map<String, List<Emoticon>>>,
    @SerialName("more")
    val more: Map<String, List<Emoticon>>,
    @SerialName("usual")
    val usual: Map<String, List<Emoticon>>
)

@Serializable
data class Emoticon(
    @SerialName("category")
    val category: String,
    @SerialName("common")
    val common: Boolean,
    @SerialName("hot")
    val hot: Boolean,
    @SerialName("icon")
    val icon: String,
    @SerialName("phrase")
    val phrase: String,
    @SerialName("picid")
    val id: String,
    @SerialName("type")
    val type: String,
    @SerialName("url")
    val url: String,
    @SerialName("value")
    val value: String
)
