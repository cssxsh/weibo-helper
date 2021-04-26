package xyz.cssxsh.weibo.data.feed

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import xyz.cssxsh.weibo.data.VerifiedType

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