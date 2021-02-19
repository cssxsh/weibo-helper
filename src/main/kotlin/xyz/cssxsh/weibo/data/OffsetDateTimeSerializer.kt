package xyz.cssxsh.weibo.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object OffsetDateTimeSerializer: KSerializer<OffsetDateTime> {

    private val formatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("E MMM d HH:mm:ss Z yyyy", Locale.ENGLISH)

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("OffsetDateTimeSerializerTo[$formatter]", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): OffsetDateTime =
        OffsetDateTime.parse(decoder.decodeString(), formatter)

    override fun serialize(encoder: Encoder, value: OffsetDateTime) =
        encoder.encodeString(value.format(formatter))

}