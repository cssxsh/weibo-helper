package xyz.cssxsh.weibo

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import xyz.cssxsh.mirai.plugin.*
import xyz.cssxsh.weibo.api.*
import java.io.File

internal class ProfileKtTest : WeiboClientTest() {

    @BeforeEach
    fun flush(): Unit = runBlocking { client.restore() }

    @Test
    fun getUserInfo(): Unit = runBlocking {
        val uid = 6179286709
        client.getUserInfo(uid).let {
            assertEquals(uid, it.user.id)
            println(it)
        }
    }

    @Test
    fun getUserDetail(): Unit = runBlocking {
        client.getUserDetail(uid = 6850282182).let {
            println(it)
        }
    }

    @Test
    fun getUserHistory(): Unit = runBlocking {
        client.getUserHistory(uid = 6850282182).let {
            println(it)
        }
    }

    @Test
    fun getUserHot(): Unit = runBlocking {
        client.getUserHot(uid = 6850282182, page = 1).let {
            println(it)
        }
    }

    @Test
    fun getGroupMembers(): Unit = runBlocking {
        client.getGroupMembers(gid = 4056713441256071, page = 1).users.forEach {
            println(it)
        }
    }

    private val ImageCache = File("F:\\WeiboCache")

    @Test
    fun cache(): Unit = runBlocking {
        val gid = 4056713441256071
        val members = flow {
            var page = 1
            while (isActive) {
                runCatching {
                    client.getGroupMembers(gid = gid, page = page++)
                }.onSuccess {
                    if (it.users.isEmpty()) return@flow
                    emitAll(it.users.asFlow())
                }.onFailure {
                    println(it.message)
                }.getOrNull() ?: break
            }
        }
        members.collect { info ->
            println(info)
//            val interval = (3).seconds
//            val history = client.getUserHistory(info.id)
//            val months = history.flatMap { (year, months) -> months.map { YearMonth.of(year, it)!! } }.sortedDescending()
//            var count = 0
//            months.forEach { month ->
//                runCatching {
//                    client.getRecord(month, interval).onEach { blog ->
//                        blog.getImages(flush = false)
//                    }
//                }.onSuccess { record ->
//                    count += record.size
//                    if (record.isNotEmpty()) println("对@${info.screen}的${month}缓存下载完成, Total: ${record.size}")
//                }.onFailure {
//                    logger.warning("对@${info.screen}的${month}缓存下载失败", it)
//                }
//            }
//            println("对@${info.screen}的缓存下载完成, ${count}/${info.statusesCount}")
        }
    }

    @Test
    fun group(): Unit = runBlocking {
        client.getUserInfo()
        ImageCache.listFiles().orEmpty().filter { cache ->
            cache.resolve("avatar.ico").exists().not() && cache.resolve("desktop.ini").run {
                "SHELL32" in readText()
            }
        }.map {
            it.name.toLong()
        }.onEach {
            runCatching {
                client.getUserInfo(it).user.apply {
                    if (following.not()) {
                        client.follow(it)
                    }
                }
            }.onSuccess {
                println(it.id)
                delay(3 * 1000L)
            }.mapCatching { info ->
                client.setGroup(user = info.id, group = 4056713441256071)
                val cache = ImageCache.resolve("${info.id}").apply { mkdirs() }
                cache.desktop(user = info)
                client.getGroupList(info.id)
            }.onFailure {
                println(it.message)
            }.onSuccess {
                println(it)
            }
        }
    }
}