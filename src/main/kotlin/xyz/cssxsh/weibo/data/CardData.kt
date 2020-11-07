package xyz.cssxsh.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class CardData(
    @SerialName("data")
    val `data`: Data,
    @SerialName("ok")
    val ok: Int
) {
    @Serializable
    data class Data(
        @SerialName("banners")
        val banners: JsonElement? = null,
        @SerialName("cardlistInfo")
        val cardListInfo: CardListInfo,
        @SerialName("cards")
        val cards: List<JsonElement>,
        @SerialName("scheme")
        val scheme: String,
        @SerialName("showAppTips")
        val showAppTips: Int
    ) {
        @Serializable
        data class CardListInfo(
            @SerialName("containerid")
            val containerId: String,
            @SerialName("show_style")
            val showStyle: Int,
            @SerialName("since_id")
            val sinceId: Long,
            @SerialName("total")
            val total: Int,
            @SerialName("v_p")
            val vP: Int
        )
    }
}