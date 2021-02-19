package xyz.cssxsh.weibo.data.blog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class PictureInfo(
    @SerialName("bmiddle")
    val middle: Picture,
    @SerialName("large")
    val large: Picture,
    @SerialName("largest")
    val largest: Picture,
    @SerialName("mw2000")
    val mw2000: Picture,
    @SerialName("object_id")
    val objectId: String,
    @SerialName("original")
    val original: Picture,
    @SerialName("thumbnail")
    val thumbnail: Picture,
    @SerialName("focus_point")
    val focusPoint: JsonObject? = null,
    @SerialName("photo_tag")
    val photoTag: Int,
    @SerialName("pic_id")
    val pictureId: String,
    @SerialName("pic_status")
    val pictureStatus: Int,
    @SerialName("type")
    val type: String
)
