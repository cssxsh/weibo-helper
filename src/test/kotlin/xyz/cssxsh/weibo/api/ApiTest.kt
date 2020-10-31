package xyz.cssxsh.weibo.api

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.junit.jupiter.api.Test
import xyz.cssxsh.weibo.WeiboClient
import xyz.cssxsh.weibo.data.TagCard
import xyz.cssxsh.weibo.data.BlogCard

internal class ApiTest {

    private val uids = listOf(
        6279793937,
        6882830889,
        5721376081
    )

    @Test
    fun userData(): Unit = runBlocking {
        WeiboClient(emptyMap()).run {
            uids.forEach {
                println(userData(it))
            }
        }
    }

    @Test
    fun cardData(): Unit = runBlocking {
        WeiboClient(emptyMap()).run {
            uids.forEach {
                cardData(it).getBlogs()
            }
        }
    }
}