package xyz.cssxsh.weibo

import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.jupiter.api.Test
import xyz.cssxsh.weibo.api.*

internal class FeedKtTest: WeiboClientTest() {

    @Before
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
        client.getGroupsTimeline(gid = 4056713441256071L).statuses.forEach { blog ->
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
}