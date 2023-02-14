package xyz.cssxsh.weibo.data

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.jsoup.Jsoup
import java.time.*

@Serializable
public data class SuperChatData(
    @SerialName("cards")
    val cards: List<SuperChatCard> = emptyList(),
    @SerialName("pageInfo")
    val info: SuperChatPageInfo,
    @SerialName("scheme")
    val scheme: String = "",
    @SerialName("showAppTips")
    val showAppTips: Int = 0
)

@Serializable
public data class SuperChatCard(
    @SerialName("card_group")
    val group: List<SuperChatCardGroup> = emptyList(),
    @SerialName("mblog")
    val blog: SuperChatMicroBlog? = null,
    @SerialName("card_type")
    val type: String = ""
)

@Serializable
public data class SuperChatCardGroup(
    @SerialName("card_type")
    val type: String = "",
    @SerialName("card_type_name")
    val name: String = "",
    @SerialName("mblog")
    val blog: SuperChatMicroBlog? = null
)

@Serializable
public data class SuperChatMicroBlog(
    @SerialName("attitudes_count")
    val attitudes: Int = 0,
    @SerialName("bid")
    val bid: String = "",
    @SerialName("comments_count")
    val comments: Int = 0,
    @SerialName("created_at")
    @Serializable(WeiboDateTimeSerializer::class)
    val created: OffsetDateTime,
    @SerialName("favorited")
    val favorited: Boolean = false,
    @SerialName("id")
    val id: Long,
    @SerialName("isLongText")
    val isLongText: Boolean = false,
    @SerialName("pic_ids")
    val pictures: List<String> = emptyList(),
    @SerialName("reposts_count")
    val reposts: Int = 0,
    @SerialName("text")
    val html: String = "",
    @SerialName("user")
    val user: MicroBlogUser? = null
) {
    public fun toMicroBlog(): MicroBlog {
        val text = html
            .replace("""<img alt="(.+?)".+?/>""".toRegex()) { it.groupValues[1] }
            .let { Jsoup.parse(it).wholeText() }
        return MicroBlog(
            attitudes = attitudes,
            comments = comments,
            created = created,
            favorited = favorited,
            id = id,
            mid = bid,
            pictures = pictures,
            reposts = reposts,
            raw = text,
            user = user,
            continueTag = if (isLongText) JsonNull else null
        )
    }
}

@Serializable
public data class SuperChatPageInfo(
    @SerialName("containerid")
    val containerId: String,
    @SerialName("desc")
    val description: String,
    @SerialName("detail_desc")
    val detail: String = "",
    @SerialName("nick")
    val nick: String = "",
    @SerialName("oid")
    val oid: String = "",
    @SerialName("page_size")
    val size: Int = 0,
    @SerialName("page_title")
    val title: String = "",
    @SerialName("page_url")
    val url: String = "",
    @SerialName("since_id")
    val sinceId: Long = 0,
    @SerialName("total")
    val total: Int = 0
)