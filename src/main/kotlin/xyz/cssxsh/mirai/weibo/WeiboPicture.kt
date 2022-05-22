package xyz.cssxsh.mirai.weibo

import kotlinx.serialization.*

@Serializable
sealed class WeiboPicture {

    @SerialName("none")
    @Serializable
    class None : WeiboPicture()


    @SerialName("all")
    @Serializable
    class All : WeiboPicture()

    /**
     * 超过limit图片，其余已省略
     */
    @SerialName("limit")
    @Serializable
    class Limit(val total: Int = 3) : WeiboPicture()

    /**
     * "超过三张图片，全部省略"
     */
    @SerialName("top")
    @Serializable
    class Top(val total: Int = 3) : WeiboPicture()
}
