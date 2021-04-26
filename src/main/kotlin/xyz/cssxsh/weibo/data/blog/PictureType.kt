package xyz.cssxsh.weibo.data.blog

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = PictureType.Companion::class)
enum class PictureType(val value: String) {
    PICTURE(value = "pic"),
    GIF(value = "gif"),
    LIVE_PHOTO(value = "livephoto");

    companion object : KSerializer<PictureType> {

        override val descriptor: SerialDescriptor =
            buildSerialDescriptor(PictureType::class.qualifiedName!!, SerialKind.ENUM)

        override fun serialize(encoder: Encoder, value: PictureType) =
            encoder.encodeString(value.value)

        override fun deserialize(decoder: Decoder): PictureType = decoder.decodeString().let { value ->
            requireNotNull(values().find { it.value == value }) { decoder.decodeString() }
        }
    }
}