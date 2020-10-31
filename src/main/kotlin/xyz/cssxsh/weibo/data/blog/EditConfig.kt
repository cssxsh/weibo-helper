package xyz.cssxsh.weibo.data.blog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EditConfig(
    @SerialName("edited")
    val edited: Boolean,
    @SerialName("menu_edit_history")
    val menuEditHistory: MenuEditHistory? = null
) {
    @Serializable
    data class MenuEditHistory(
        @SerialName("scheme")
        val scheme: String,
        @SerialName("title")
        val title: String
    )
}