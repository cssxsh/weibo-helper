package xyz.cssxsh.mirai.weibo.data

import kotlinx.serialization.*
import java.time.*

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