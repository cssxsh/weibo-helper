package xyz.cssxsh.mirai.plugin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.*

@Serializable
data class WeiboTaskInfo(
    @SerialName("last")
    val last: Long = 9999999999999999L,
    @SerialName("min_interval_millis")
    val minIntervalMillis: Long = (5).minutes.toLongMilliseconds(),
    @SerialName("max_interval_millis")
    val maxIntervalMillis: Long = (10).minutes.toLongMilliseconds(),
    @SerialName("contacts")
    val contacts: List<ContactInfo> = emptyList()
) {

    enum class ContactType {
        GROUP,
        FRIEND
    }

    @Serializable
    data class ContactInfo(
        @SerialName("id")
        val id: Long,
        @SerialName("bot")
        val bot: Long,
        @SerialName("type")
        val type: ContactType
    )

    fun getInterval() = minIntervalMillis..maxIntervalMillis
}