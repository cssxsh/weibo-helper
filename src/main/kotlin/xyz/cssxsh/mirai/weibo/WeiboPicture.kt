package xyz.cssxsh.mirai.weibo

import kotlinx.serialization.*

@Serializable
public sealed class WeiboPicture {

    @SerialName("none")
    @Serializable
    public class None : WeiboPicture()


    @SerialName("all")
    @Serializable
    public class All : WeiboPicture()

    /**
     * 超过limit图片，其余已省略
     */
    @SerialName("limit")
    @Serializable
    public class Limit(public val total: Int = 3) : WeiboPicture()

    /**
     * "超过三张图片，全部省略"
     */
    @SerialName("top")
    @Serializable
    public class Top(public val total: Int = 3) : WeiboPicture()
}
