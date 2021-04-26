package xyz.cssxsh.weibo.data.profile

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = GenderType.Companion::class)
enum class GenderType(val value: String) {
    MALE(value = "m"),
    FEMALE(value = "f"),
    NONE(value = "n");

    companion object : KSerializer<GenderType> {

        override val descriptor: SerialDescriptor =
            buildSerialDescriptor(GenderType::class.qualifiedName!!, SerialKind.ENUM)

        override fun serialize(encoder: Encoder, value: GenderType) =
            encoder.encodeString(value.value)

        override fun deserialize(decoder: Decoder): GenderType = decoder.decodeString().let { value ->
            requireNotNull(values().find { it.value == value }) { decoder.decodeString() }
        }
    }
}