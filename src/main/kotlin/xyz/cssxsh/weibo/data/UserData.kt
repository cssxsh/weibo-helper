package xyz.cssxsh.weibo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class UserData(
    @SerialName("data")
    val `data`: Data,
    @SerialName("ok")
    val ok: Int
) {
    @Serializable
    data class Data(
        @SerialName("avatar_guide")
        val avatarGuide: List<JsonElement>,
        @SerialName("fans_scheme")
        val fansScheme: String,
        @SerialName("follow_scheme")
        val followScheme: String,
        @SerialName("isStarStyle")
        val isStarStyle: Int,
        @SerialName("scheme")
        val scheme: String,
        @SerialName("showAppTips")
        val showAppTips: Int,
        @SerialName("tabsInfo")
        val tabsInfo: TabsInfo,
        @SerialName("userInfo")
        val userInfo: UserInfo
    ) {
        @Serializable
        data class TabsInfo(
            @SerialName("selectedTab")
            val selectedTab: Int,
            @SerialName("tabs")
            val tabs: List<Tab>
        ) {
            @Serializable
            data class Tab(
                @SerialName("apipath")
                val apiPath: String? = null,
                @SerialName("containerid")
                val containerId: String,
                @SerialName("hidden")
                val hidden: Int,
                @SerialName("id")
                val id: Int,
                @SerialName("must_show")
                val mustShow: Int,
                @SerialName("tabKey")
                val tabKey: String,
                @SerialName("tab_type")
                val tabType: String,
                @SerialName("title")
                val title: String,
                @SerialName("url")
                val url: String? = null
            )
        }

        @Serializable
        data class UserInfo(
            @SerialName("avatar_hd")
            val avatarHd: String,
            @SerialName("close_blue_v")
            val closeBlueV: Boolean,
            @SerialName("cover_image_phone")
            val coverImagePhone: String,
            @SerialName("description")
            val description: String,
            @SerialName("follow_count")
            val followCount: Int,
            @SerialName("follow_me")
            val followMe: Boolean,
            @SerialName("followers_count")
            val followersCount: Int,
            @SerialName("following")
            val following: Boolean,
            @SerialName("gender")
            val gender: String,
            @SerialName("id")
            val id: Long,
            @SerialName("like")
            val like: Boolean,
            @SerialName("like_me")
            val likeMe: Boolean,
            @SerialName("mbrank")
            val mbRank: Int,
            @SerialName("mbtype")
            val mbType: Int,
            @SerialName("profile_image_url")
            val profileImageUrl: String,
            @SerialName("profile_url")
            val profileUrl: String,
            @SerialName("screen_name")
            val screenName: String,
            @SerialName("statuses_count")
            val statusesCount: Int,
            @SerialName("toolbar_menus")
            val toolbarMenus: List<JsonElement>,
            @SerialName("urank")
            val uRank: Int,
            @SerialName("verified")
            val verified: Boolean,
            @SerialName("verified_reason")
            val verifiedReason: String,
            @SerialName("verified_type")
            val verifiedType: Int,
            @SerialName("verified_type_ext")
            val verifiedTypeExt: Int
        )
    }
}