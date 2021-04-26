package xyz.cssxsh.weibo.data.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class UserDetail(
    @SerialName("birthday")
    val birthday: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("description")
    val description: String,
    @SerialName("followers")
    val followers: Followers,
    @SerialName("gender")
    val gender: GenderType,
    @SerialName("interaction")
    val interaction: Interaction,
    @SerialName("location")
    val location: String,
    @SerialName("desc_text")
    val verifiedText: String,
    @SerialName("verified_url")
    val verifiedUrl: String,
    @SerialName("label_desc")
    private val labelDescription: List<JsonObject>,
    @SerialName("sunshine_credit")
    private val sunshineCredit: JsonObject
) {
    @Serializable
    data class SimpleUser(
        @SerialName("avatar_large")
        val avatarLarge: String,
        @SerialName("id")
        val id: Long,
        @SerialName("screen_name")
        val screen: String
    )

    @Serializable
    data class Followers(
        @SerialName("total_number")
        val total: Int,
        @SerialName("users")
        val users: List<SimpleUser>
    )

    @Serializable
    data class Interaction(
        @SerialName("date")
        val date: String,
        @SerialName("interaction")
        val interaction: Int,
        @SerialName("pre_read_user_count")
        val preReadUserCount: Int,
        @SerialName("read_count")
        val readCount: Int,
        @SerialName("read_user_count")
        val readUserCount: Int,
        @SerialName("uid")
        val uid: Long
    )
}