package xyz.cssxsh.weibo.data.blog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class PictureInfo(
    @SerialName("pic_id")
    val id: String,
    @SerialName("bmiddle")
    val middle: Picture,
    @SerialName("large")
    val large: Picture,
    @SerialName("largest")
    val largest: Picture,
    @SerialName("mw2000")
    val mw2000: Picture,
    @SerialName("original")
    val original: Picture,
    @SerialName("thumbnail")
    val thumbnail: Picture,
    @SerialName("blur")
    val blur: Picture? = null,
    /**
     * TODO PictureStatus
     */
    @SerialName("pic_status")
    val status: Int,
    @SerialName("type")
    val type: PictureType,
    @SerialName("actionlog")
    private val actionLog: List<JsonObject> = emptyList(),
    @SerialName("button_name")
    private val buttonName: String? = null,
    @SerialName("button_scheme")
    private val buttonScheme: String? = null,
    @SerialName("focus_point")
    private val focusPoint: JsonObject? = null,
    @SerialName("fid")
    private val fid: String? = null,
    @SerialName("filter_id")
    private val filterId: String? = null,
    @SerialName("object_id")
    private val objectId: String,
    @SerialName("photo_tag")
    private val photoTag: Int,
    @SerialName("sticker_id")
    private val stickerId: String? = null,
    @SerialName("pic_tags")
    private val tags: List<JsonObject> = emptyList(),
    @SerialName("video")
    private val video: String? = null,
    @SerialName("video_object_id")
    private val videoObjectId: String? = null
)
