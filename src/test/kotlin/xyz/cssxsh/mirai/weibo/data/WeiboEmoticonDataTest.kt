package xyz.cssxsh.mirai.weibo.data

import org.junit.jupiter.api.Test

internal class WeiboEmoticonDataTest {

    @Test
    fun default() {
        WeiboEmoticonData.default().forEach {
            println(it.phrase)
        }
    }
}