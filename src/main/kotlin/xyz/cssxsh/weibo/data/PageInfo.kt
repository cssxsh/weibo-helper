package xyz.cssxsh.weibo.data

import kotlinx.serialization.*

@Serializable
data class PageInfo(
    @SerialName("media_info")
    val media: MediaInfo? = null,
    @SerialName("object_type")
    val form: ObjectType = ObjectType.NONE,
    @SerialName("page_id")
    val id: String,
    @SerialName("page_pic")
    val picture: String,
    @SerialName("page_title")
    val title: String,
    @SerialName("type")
    val type: Int
) {

    @Serializable
    data class MediaInfo(
        @SerialName("author_info")
        val author: UserInfo,
        @SerialName("duration")
        val duration: Int,
        @SerialName("h5_url")
        val url: String,
        @SerialName("media_id")
        val id: Long,
        @SerialName("name")
        val name: String,
        @SerialName("next_title")
        val title: String,
        @SerialName("online_users_number")
        val online: Int,
        @SerialName("playback_list")
        val playbacks: List<PlayBack>,
        @SerialName("titles")
        val titles: List<Title> = emptyList(),
        @SerialName("video_publish_time")
        val published: Long
    ) {

        @Serializable
        data class PlayBack(
            @SerialName("play_info")
            val info: PlayInfo
        )

        @Serializable
        data class PlayInfo(
            @SerialName("height")
            val height: Int,
            @SerialName("mime")
            val mime: String,
            @SerialName("quality_label")
            val quality: String,
            @SerialName("size")
            val size: Int = 0,
            @SerialName("tcp_receive_buffer")
            val buffer: Int,
            @SerialName("type")
            val type: Int,
            @SerialName("url")
            val url: String = "",
            @SerialName("width")
            val width: Int
        )
    }

    @Serializable
    data class Title(
        @SerialName("default")
        val default: Boolean = false,
        @SerialName("title")
        val title: String
    )
}