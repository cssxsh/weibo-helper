package xyz.cssxsh.mirai.weibo.data

import org.junit.jupiter.api.*

internal class WeiboEmoticonDataTest {

    @Test
    fun default() {
        WeiboEmoticonData.default().forEach {
            println(it.key)
        }
    }
}