package xyz.cssxsh.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * cardType = 11
 */
@Serializable
data class TagCard(
    @SerialName("card_group")
    val cardGroup: List<CardGroup>,
    @SerialName("card_style")
    val cardStyle: Int,
    @SerialName("card_type")
    val cardType: Int,
    @SerialName("display_arrow")
    val displayArrow: Int,
    @SerialName("skip_group_title")
    val skipGroupTitle: Boolean
) {
    @Serializable
    data class CardGroup(
        @SerialName("card_type")
        val cardType: Int,
        @SerialName("col")
        val col: Int,
        @SerialName("group")
        val group: List<Group>
    ) {
        @Serializable
        data class Group(
            @SerialName("action_log")
            val actionLog: ActionLog,
            @SerialName("icon")
            val icon: String? = null,
            @SerialName("scheme")
            val scheme: String,
            @SerialName("title_sub")
            val titleSub: String,
            @SerialName("word_scheme")
            val wordScheme: String
        ) {
            @Serializable
            data class ActionLog(
                @SerialName("act_code")
                val actCode: Int,
                @SerialName("ext")
                val ext: String,
                @SerialName("fid")
                val fid: String,
                @SerialName("oid")
                val oid: String
            )
        }
    }
}