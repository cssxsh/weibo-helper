package xyz.cssxsh.weibo

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import xyz.cssxsh.mirai.plugin.*
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

    private suspend fun SimpleMicroBlog.getContent(): String {
        return if (isLongText) {
            runCatching {
                requireNotNull(WeiboHelperPlugin.weiboClient.getLongText(id).data) { toString() }.longTextContent!!
            }.getOrElse {
                textRaw ?: Jsoup.parse(text).text()
            }
        } else {
            textRaw ?: Jsoup.parse(text).text()
        }
    }

    private suspend fun SimpleMicroBlog.buildText() = buildString {
        appendLine("微博 $username 有新动态：")
        appendLine("时间: $createdAt")
        appendLine("链接: $url")
        appendLine(getContent())
        getImageUrls().forEach {
            appendLine(it)
        }
        retweeted?.let { retweeted ->
            appendLine("==============================")
            appendLine("@${retweeted.username}")
            appendLine("时间: ${retweeted.createdAt}")
            appendLine("链接: ${retweeted.url}")
            appendLine(retweeted.getContent())
            retweeted.getImageUrls().forEach {
                appendLine(it)
            }
        }
    }

    @Test
    fun loginTest(): Unit = runBlocking {
        client.login().let {
            Assertions.assertTrue(it.result)
            println(it)
        }
    }

    @Test
    fun getUserMicroBlogsTest(): Unit = runBlocking {
        client.login()
        list.forEach { uid ->
            client.getUserMicroBlogs(uid).getBlogs().forEach {
                Assertions.assertEquals(uid, it.user?.id)
                println(it.buildText())
            }
        }
    }

    @Test
    fun getMicroBlogTest(): Unit = runBlocking {
        client.getMicroBlog(4599751155390723L).let {
            Assertions.assertEquals(4599751155390723L, it.id)
            println(it.buildText())
        }
    }

    @Test
    fun getUserInfoTest(): Unit = runBlocking {
        client.getUserInfo(6850282182).let {
            Assertions.assertEquals(6850282182, it.data?.user?.id)
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
        client.getTimeline(100013603567911L).statuses.forEach {
            client.getUserInfo(it.user?.id)
            println(it.buildText())
        }
    }
}