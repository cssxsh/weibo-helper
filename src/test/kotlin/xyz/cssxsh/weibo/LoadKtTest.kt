package xyz.cssxsh.weibo

import kotlinx.serialization.decodeFromString
import org.junit.jupiter.api.Test
import xyz.cssxsh.weibo.data.MicroBlog
import java.io.File
import java.math.BigInteger

internal class LoadKtTest {

    private fun String.decodeBase(base: Int): BigInteger = fold((0).toBigInteger()) { acc, char ->
        acc * base.toBigInteger() + EncodeChars.indexOf(char).toBigInteger()
    }

    private val cache = File("F:\\WeiboCache\\7595188185\\")

    private val record by lazy {
        WeiboClient.Json.decodeFromString<List<MicroBlog>>(cache.resolve("2021-05.json").readText())
    }

    @Test
    fun info() {
//        "005z tYz7 gy1g qx0z 9nih jg"
//        val mb1 = record.first { it.id == 4433578743742912 }
//        val mb2 = record.first { it.id == 4432913661460226 }
//        val mb3 = record.first { it.id == 4432863123997439 }
//        val mb4 = record.first { it.id == 4432812528393179 }
//
//        println(mb1.created.toEpochSecond() - mb2.created.toEpochSecond())
//        println(mb3.created.toEpochSecond() - mb4.created.toEpochSecond())
//
//        val base = 36
//
//        val pid1 = mb1.pictures.first().substring(8..16).decodeBase(base)
//        val pid2 = mb2.pictures.first().substring(8..16).decodeBase(base)
//        val pid3 = mb3.pictures.first().substring(8..16).decodeBase(base)
//        val pid4 = mb4.pictures.first().substring(8..16).decodeBase(base)
//
//        println((pid1 - pid2))
//        println((pid3 - pid4))
        record.forEach {
            println(it.pictures.map(::image))
        }
    }
}