package xyz.cssxsh.weibo.data

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*
import java.time.*
import java.time.format.*
import java.util.*

public typealias HistoryInfo = Map<Int, List<Int>>

@Serializable
public data class SetResult(
    @SerialName("result")
    val result: Boolean
)

public object WeiboDateTimeSerializer : KSerializer<OffsetDateTime> {

    private val formatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("E MMM d HH:mm:ss Z yyyy", Locale.ENGLISH)

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(OffsetDateTime::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): OffsetDateTime = OffsetDateTime.parse(decoder.decodeString(), formatter)

    override fun serialize(encoder: Encoder, value: OffsetDateTime): Unit = encoder.encodeString(value.format(formatter))

}

public object LocaleSerializer : KSerializer<Locale> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(Locale::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Locale = Locale(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: Locale): Unit = encoder.encodeString(value.language)
}

public object NumberToBooleanSerializer : KSerializer<Boolean> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("NumberToBooleanSerializer", PrimitiveKind.BOOLEAN)

    override fun deserialize(decoder: Decoder): Boolean = decoder.decodeLong() != 0L

    override fun serialize(encoder: Encoder, value: Boolean): Unit = encoder.encodeLong(if (value) 1L else 0L)
}

public interface WeiboValue<T> {
    public val value: T
}

public class WeiboEnumSerializer<E, T>(private val values: Array<E>) :
    KSerializer<E> where E : Enum<E>, E : WeiboValue<T> {

    override val descriptor: SerialDescriptor = JsonPrimitive.serializer().descriptor

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

@Suppress("FunctionName")
public inline fun <reified E, T> WeiboEnumSerializer(): WeiboEnumSerializer<E, T> where E : Enum<E>, E : WeiboValue<T> {
    return WeiboEnumSerializer(enumValues())
}

@Serializable(with = PictureType.Companion::class)
public enum class PictureType(override val value: String) : WeiboValue<String> {
    PICTURE(value = "pic"),
    GIF(value = "gif"),
    LIVE_PHOTO(value = "livephoto");

    public companion object : KSerializer<PictureType> by WeiboEnumSerializer()
}

@Serializable(with = GenderType.Companion::class)
public enum class GenderType(override val value: String) : WeiboValue<String> {
    MALE(value = "m"),
    FEMALE(value = "f"),
    NONE(value = "n");

    public companion object : KSerializer<GenderType> by WeiboEnumSerializer()
}

@Serializable(with = UserGroupType.Companion::class)
public enum class UserGroupType(override val value: Int) : WeiboValue<Int> {
    USER(value = 0),
    ALL(value = 1),
    QUIETLY(value = 5),
    MUTUAL(value = 9),
    GROUP(value = 10),
    FILTER(value = 20),
    SYSTEM(value = 8888);

    public companion object : KSerializer<UserGroupType> by WeiboEnumSerializer()
}

/**
 * verified_type < 8 ? "微博官方认证" : "微博个人认证"
 */
@Serializable(with = VerifiedType.Companion::class)
public enum class VerifiedType(override val value: Int) : WeiboValue<Int> {
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

    public companion object : KSerializer<VerifiedType> by WeiboEnumSerializer()
}