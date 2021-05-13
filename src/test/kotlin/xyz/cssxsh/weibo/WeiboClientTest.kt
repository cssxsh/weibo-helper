package xyz.cssxsh.weibo

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import xyz.cssxsh.weibo.api.*
import xyz.cssxsh.weibo.data.*
import java.io.File

internal class WeiboClientTest {

    private val list = listOf(
        5174017612L,
        6787924129L
    )

    private val client = WeiboClient(
        Json.decodeFromString(
            ListSerializer(HttpCookie.serializer()),
            File("./test/cookies.json").readText()
        )
    )

    private fun MicroBlog.buildText() = buildString {
        appendLine("微博 $username 有新动态：")
        appendLine("时间: $createdAt")
        appendLine("链接: $url")
        appendLine(textRaw)
        pictureInfos.forEach { (_, picture) ->
            appendLine("${picture.type}-${picture.status}-${picture.original.url}")
        }
        retweeted?.let { retweeted ->
            appendLine("==============================")
            appendLine("@${retweeted.username}")
            appendLine("时间: ${retweeted.createdAt}")
            appendLine("链接: ${retweeted.url}")
            appendLine(retweeted.textRaw)
            retweeted.pictureInfos.forEach { (_, picture) ->
                appendLine("${picture.type}-${picture.status}-${picture.original.url}")
            }
        }
    }

    @Test
    fun loginTest(): Unit = runBlocking {
        client.login().let {
            assertTrue(it.result)
            println(it)
        }
    }

    @Test
    fun getUserMicroBlogsTest(): Unit = runBlocking {
        client.login()
        list.forEach { uid ->
            client.getUserMicroBlogs(uid).list.forEach {
                assertEquals(uid, it.user?.id)
                println(it.buildText())
            }
        }
    }

    @Test
    fun getMicroBlogTest(): Unit = runBlocking {
        client.getMicroBlog(4607349879472852L).let {
            assertEquals(4607349879472852L, it.id)
            println(it.buildText())
        }
    }

    @Test
    fun getUserInfoTest(): Unit = runBlocking {
        client.getUserInfo(uid = 6179286709).let {
            assertEquals(6179286709, it.user.id)
            println(it)
        }
    }
    @Test
    fun getUserDetailTest(): Unit = runBlocking {
        client.getUserDetail(uid = 6850282182).let {
            assertEquals(6850282182, it.interaction.uid)
            println(it)
        }
    }

    @Test
    fun getUserHistoryTest(): Unit = runBlocking {
        client.login()
        client.getUserHistory(uid = 6850282182).let {
            println(it)
        }
    }

    @Test
    fun getFeedGroupsTest(): Unit = runBlocking {
        client.login()
        client.getFeedGroups().groups.forEach { group ->
            println("===${group.title}:${group.type}===")
            group.list.forEach { item ->
                println("${item.title}:${item.type} -> ${item.gid}")
            }
        }
    }

    @Test
    fun getTimelineTest(): Unit = runBlocking {
        client.login()
        client.getTimeline(gid = 4056713441256071L, count = 100, type = TimelineType.GROUPS).statuses.forEach { blog ->
            blog.user?.let { client.getUserInfo(it.id) }
            println(blog.buildText())
        }
    }

    @Test
    fun getHotTest(): Unit = runBlocking {
        client.login()
        client.getHot(gid = 102803L).statuses.forEach { blog ->
            blog.user?.let { client.getUserInfo(it.id) }
            println(blog.buildText())
        }
    }

    @Test
    fun getUserMentionsTest(): Unit = runBlocking {
        client.login()
        client.getUserMentions()
    }
}