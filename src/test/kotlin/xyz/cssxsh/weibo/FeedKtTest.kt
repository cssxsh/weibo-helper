package xyz.cssxsh.weibo

import kotlinx.coroutines.*
import org.junit.jupiter.api.*
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*
import java.io.*

internal class FeedKtTest : WeiboClientTest() {

    @BeforeEach
    fun flush(): Unit = runBlocking { client.restore() }

    @Test
    fun getFeedGroups(): Unit = runBlocking {
        client.getFeedGroups(isNewSegment = false, fetchHot = false).groups.forEach { group ->
            println("===${group.title}:${group.type}===")
            group.list.forEach { item ->
                println("${item.title}:${item.type} -> ${item.gid}")
            }
        }
    }

    @Test
    fun getTimeline(): Unit = runBlocking {
        client.getGroupsTimeline(gid = 4056713441256071).statuses.forEach { blog ->
            blog.user?.let { client.getUserInfo(it.id) }
            println(blog.toText())
        }
    }

    @Test
    fun getHot(): Unit = runBlocking {
        client.getHotTimeline(gid = 102803L).statuses.forEach { blog ->
            blog.user?.let { client.getUserInfo(it.id) }
            println(blog.toText())
        }
    }

    @Test
    fun getUser(): Unit = runBlocking {
        val json = folder.resolve("user.json").readText()
        WeiboClient.Json.decodeFromString(UserBlog.serializer(), json)
    }

    @Test
    fun getVideo(): Unit = runBlocking {
        val media = client.getMicroBlog(mid = "L4sWWErGL").page!!.media!!
        val title = media.titles.firstOrNull()?.title ?: media.title
        val video = media.playbacks.first().info
        val mp4 = File("./test/${title}.mp4")

        client.download(video = video).collect {
            mp4.appendBytes(it)
        }
    }
}