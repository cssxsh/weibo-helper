package xyz.cssxsh.mirai.plugin.data

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class WeiboEmoticonDataTest {

    @Test
    fun default() {
        WeiboEmoticonData.default().forEach {
            println(it.phrase)
        }
    }
}