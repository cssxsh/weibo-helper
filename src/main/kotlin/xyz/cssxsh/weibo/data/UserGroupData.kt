package xyz.cssxsh.weibo.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject

@Serializable
data class UserGroupData(
    @SerialName("feed_default")
    @Serializable(NumberToBooleanSerializer::class)
    val feedDefault: Boolean,
    @SerialName("fetch_hot")
    @Serializable(NumberToBooleanSerializer::class)
    val fetchHot: Boolean,
    @SerialName("groups")
    val groups: List<Group>,
    @SerialName("is_new_segment")
    @Serializable(NumberToBooleanSerializer::class)
    val isNewSegment: Boolean,
    @SerialName("ok")
    @Serializable(NumberToBooleanSerializer::class)
    val ok: Boolean = true,
    @SerialName("total_number")
    val total: Int
)

@Serializable
data class Group(
    @SerialName("group")
    val list: List<UserGroup>,
    @SerialName("group_type")
    val type: Int,// XXX
    @SerialName("priority")
    @Serializable(NumberToBooleanSerializer::class)
    val priority: Boolean = false,
    @SerialName("title")
    val title: String
)

@Serializable
data class UserGroup(
    @SerialName("count")
    val count: Int,
    @SerialName("frequency")
    @Serializable(NumberToBooleanSerializer::class)
    val frequency: Boolean,
    @SerialName("gid")
    val gid: Long,
    @SerialName("is_unread")
    @Serializable(NumberToBooleanSerializer::class)
    val isUnread: Boolean = false,
    @SerialName("title")
    val title: String,
    @SerialName("type")
    val type: UserGroupType,
    @SerialName("uid")
    val uid: Long,
    @SerialName("fid")
    private val fid: String? = null,
    @SerialName("containerid")
    private val containerId: String? = null,
    @SerialName("apipath")
    private val apiPath: String? = null,
    @SerialName("can_edit")
    @Serializable(NumberToBooleanSerializer::class)
    private val canEdit: Boolean = false,
    @SerialName("ad_scene")
    @Serializable(NumberToBooleanSerializer::class)
    private val advertisementScene: Boolean,
    @SerialName("settings")
    private val settings: JsonObject? = null,
    @SerialName("sysgroup")
    private val systemGroup: Int,
    @SerialName("navigation_title")
    private val navigationTitle: String? = null,
    @SerialName("open_scheme")
    private val openScheme: Boolean = false,
)

@Serializable(with = UserGroupType.Companion::class)
enum class UserGroupType(val value: Int) {
    USER(value = 0),
    ALL(value = 1),
    QUIETLY(value = 5),
    MUTUAL(value = 9),
    GROUP(value = 10),
    FILTER(value = 20),
    SYSTEM(value = 8888);

    companion object : KSerializer<UserGroupType> {
        override val descriptor: SerialDescriptor =
            buildSerialDescriptor(VerifiedType::class.qualifiedName!!, SerialKind.ENUM)

        override fun serialize(encoder: Encoder, value: UserGroupType) =
            encoder.encodeInt(value.value)

        override fun deserialize(decoder: Decoder): UserGroupType = decoder.decodeInt().let { value ->
            requireNotNull(values().find { it.value == value }) { "$value not in ${values().toList()}" }
        }
    }
}