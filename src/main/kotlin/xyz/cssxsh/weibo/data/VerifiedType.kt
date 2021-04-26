package xyz.cssxsh.weibo.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * verified_type < 8 ? "微博官方认证" : "微博个人认证"
 */
@Serializable(with = VerifiedType.Companion::class)
enum class VerifiedType(val value: Int) {
    NONE(value = -1),
    PERSONAL(value = 0),
    GOVERNMENT(value = 1),
    ENTERPRISE(value = 2),
    MEDIA(value = 3),
    CAMPUS(value = 4),
    WEBSITE(value = 5),
    APPLICATION(value = 6),
    ORGANIZATION(value = 7),
    PENDING_ENTERPRISE(value = 8),
    TEMP_9(value = 9),
    TEMP_10(value = 10),
    JUNIOR(value = 200),
    SENIOR(value = 220),
    DECEASED(value = 400);

    companion object : KSerializer<VerifiedType> {

        override val descriptor: SerialDescriptor =
            buildSerialDescriptor(VerifiedType::class.qualifiedName!!, SerialKind.ENUM)

        override fun serialize(encoder: Encoder, value: VerifiedType) =
            encoder.encodeInt(value.value)

        override fun deserialize(decoder: Decoder): VerifiedType = decoder.decodeInt().let { value ->
            requireNotNull(values().find { it.value == value }) { decoder.decodeString() }
        }
    }
}