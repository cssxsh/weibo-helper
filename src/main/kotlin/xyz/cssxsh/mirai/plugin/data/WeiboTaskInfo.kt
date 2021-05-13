package xyz.cssxsh.mirai.plugin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import kotlin.time.*

@Serializable
data class WeiboTaskInfo(
    @SerialName("last")
    @Serializable(OffsetDateTimeSerializer::class)
    val last: OffsetDateTime = OffsetDateTime.now(),
    @SerialName("name")
    val name: String,
    @SerialName("interval")
    @Serializable(LongRangeSerializer::class)
    val interval: LongRange = (5).minutes.toLongMilliseconds()..(10).minutes.toLongMilliseconds(),
    @SerialName("contacts")
    val contacts: Set<Long> = emptySet()
)