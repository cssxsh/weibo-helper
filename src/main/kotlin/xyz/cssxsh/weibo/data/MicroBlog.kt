package xyz.cssxsh.weibo.data

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.time.*

public val MicroBlog.isLongText: Boolean get() = (continueTag != null)

public val MicroBlog.hasVideo: Boolean get() = (page?.form == "video")

public val MicroBlog.hasPage: Boolean get() = (page != null)

@Serializable
public data class MicroBlog(
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
    internal val continueTag: JsonElement? = null,
    @SerialName("favorited")
    val favorited: Boolean = false,
    @SerialName("id")
    val id: Long,
    @SerialName("mblogid")
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
    val urls: List<UrlStruct> = emptyList(),
    @SerialName("title")
    val title: TopTitle? = null,
    @SerialName("screen_name_suffix_new")
    val suffix: List<ScreenSuffix>? = null,
    @SerialName("page_info")
    val page: PageInfo? = null
)

@Serializable
public data class UrlStruct(
    @SerialName("h5_target_url")
    val h5: String? = null,
    @SerialName("long_url")
    val long: String? = null,
    @SerialName("short_url")
    val short: String,
    @SerialName("url_title")
    val title: String,
    @SerialName("url_type")
    val type: String = ""
)

@Serializable
public data class TopTitle(
    @SerialName("text")
    val text: String
)

@Serializable
public data class ScreenSuffix(
    @SerialName("content")
    val content: String
)

@Serializable
public data class MicroBlogUser(
    @SerialName("avatar_hd")
    override val avatarHighDefinition: String = "",
    @SerialName("avatar_large")
    override val avatarLarge: String = "",
    @SerialName("following")
    override val following: Boolean = false,
    @SerialName("follow_me")
    val followed: Boolean = false,
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
) : UserBaseInfo

@Serializable
public data class LongTextContent(
    @SerialName("longTextContent")
    val content: String? = null,
    @SerialName("url_struct")
    val urls: List<UrlStruct> = emptyList()
)

@Serializable
public data class TimelineData(
    @SerialName("max_id")
    val maxId: Long = 0,
    @SerialName("since_id")
    val sinceId: Long = 0,
    @SerialName("statuses")
    val statuses: List<MicroBlog> = emptyList(),
)

@Serializable
public data class UserBlog(
    @SerialName("since_id")
    val sinceId: String? = null,
    @SerialName("list")
    val list: List<MicroBlog> = emptyList()
)

@Serializable
public data class EmotionData(
    @SerialName("emoticon")
    val emoticon: Map<String, Map<String, List<Emoticon>>>
)

@Serializable
public data class Emoticon(
    @SerialName("category")
    val category: String = "null",
    @SerialName("phrase")
    val phrase: String,
    @SerialName("url")
    val url: String
)

@Serializable
public data class SearchResult(
    @SerialName("cardlist_title")
    val title: String,
    @SerialName("cards")
    val cards: List<SearchResultCard>
)

@Serializable
public data class SearchResultCard(
    @SerialName("card_group")
    val group: List<SearchResultCard> = emptyList(),
    @SerialName("card_type")
    val type: Int,
    @SerialName("is_hotweibo")
    @Serializable(NumberToBooleanSerializer::class)
    val isHot: Boolean = false,
    @SerialName("mblog")
    val blog: MicroBlog? = null,
    @SerialName("user")
    val user: MicroBlogUser? = null
)