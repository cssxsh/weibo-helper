package xyz.cssxsh.weibo.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Serializer(OffsetDateTime::class)
object WeiboDateTimeSerializer : KSerializer<OffsetDateTime> {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("E MMM d HH:mm:ss Z yyyy", Locale.ENGLISH)

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("OffsetDateTimeSerializerTo[$formatter]", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): OffsetDateTime = OffsetDateTime.parse(decoder.decodeString(), formatter)

    override fun serialize(encoder: Encoder, value: OffsetDateTime) = encoder.encodeString(value.format(formatter))

}

@Serializer(Locale::class)
object LocaleSerializer : KSerializer<Locale> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocaleSerializer", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Locale = Locale(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: Locale) = encoder.encodeString(value.language)
}

@Serializer(Boolean::class)
object NumberToBooleanSerializer : KSerializer<Boolean> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("NumberToBooleanSerializer", PrimitiveKind.BOOLEAN)

    override fun deserialize(decoder: Decoder): Boolean =
        decoder.decodeLong() != 0L

    override fun serialize(encoder: Encoder, value: Boolean) =
        encoder.encodeLong(if (value) 1L else 0L)
}

interface WeiboValue<T> {
    val value: T
}

class WeiboEnumSerializer<E, T>(private val values: Array<E>) : KSerializer<E> where E : Enum<E>, E : WeiboValue<T> {

    override val descriptor: SerialDescriptor =
        buildSerialDescriptor(values.first()::class.qualifiedName!!, SerialKind.ENUM)

    override fun serialize(encoder: Encoder, value: E) {
        when (val enumValue = value.value) {
            is String -> encoder.encodeString(enumValue)
            is Int -> encoder.encodeInt(enumValue)
            is Long -> encoder.encodeLong(enumValue)
            else -> throw IllegalArgumentException("不支持的类型")
        }
    }

    override fun deserialize(decoder: Decoder): E {
        val value = when (values.first().value) {
            is String -> decoder.decodeString()
            is Int -> decoder.decodeInt()
            is Long -> decoder.decodeLong()
            else -> throw IllegalArgumentException("不支持的类型")
        }
        return requireNotNull(values.find { it.value == value }) { decoder.decodeString() }
    }
}

@Serializable(with = PictureType.Companion::class)
enum class PictureType(override val value: String) : WeiboValue<String> {
    PICTURE(value = "pic"),
    GIF(value = "gif"),
    LIVE_PHOTO(value = "livephoto");

    companion object : KSerializer<PictureType> by WeiboEnumSerializer(values())
}

@Serializable(with = GenderType.Companion::class)
enum class GenderType(override val value: String) : WeiboValue<String> {
    MALE(value = "m"),
    FEMALE(value = "f"),
    NONE(value = "n");

    companion object : KSerializer<GenderType> by WeiboEnumSerializer(values())
}

@Serializable(with = UserGroupType.Companion::class)
enum class UserGroupType(override val value: Int) : WeiboValue<Int> {
    USER(value = 0),
    ALL(value = 1),
    QUIETLY(value = 5),
    MUTUAL(value = 9),
    GROUP(value = 10),
    FILTER(value = 20),
    SYSTEM(value = 8888);

    companion object : KSerializer<UserGroupType> by WeiboEnumSerializer(values())
}

/**
 * verified_type < 8 ? "微博官方认证" : "微博个人认证"
 */
@Serializable(with = VerifiedType.Companion::class)
enum class VerifiedType(override val value: Int): WeiboValue<Int> {
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

    companion object : KSerializer<VerifiedType> by WeiboEnumSerializer(values())
}