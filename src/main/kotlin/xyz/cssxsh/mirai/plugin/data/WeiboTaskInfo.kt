package xyz.cssxsh.mirai.plugin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class WeiboTaskInfo(
    @SerialName("last")
    @Serializable(OffsetDateTimeSerializer::class)
    val last: OffsetDateTime = OffsetDateTime.now(),
    @SerialName("name")
    val name: String,
    @SerialName("contacts")
    val contacts: Set<Long> = emptySet()
)