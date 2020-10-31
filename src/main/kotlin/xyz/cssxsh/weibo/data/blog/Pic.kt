package xyz.cssxsh.weibo.data.blog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pic(
    @SerialName("geo")
    val geo: Geo,
    @SerialName("large")
    val large: Large,
    @SerialName("pid")
    val pid: String,
    @SerialName("size")
    val size: String,
    @SerialName("url")
    val url: String
) {
    @Serializable
    data class Geo(
        @SerialName("croped")
        val croped: Boolean,
        @SerialName("height")
        val height: Int,
        @SerialName("width")
        val width: Int
    )

    @Serializable
    data class Large(
        @SerialName("geo")
        val geo: Geo,
        @SerialName("size")
        val size: String,
        @SerialName("url")
        val url: String
    )
}