package xyz.cssxsh.weibo

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
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

    private val client by lazy { WeiboClient(status) }

    private val qrcode = File("./test/qrcode.jpg")

    private fun MicroBlog.buildText() = buildString {
        appendLine("微博 $username 有新动态：")
        appendLine("时间: $createdAt")
        appendLine("链接: $link")
        appendLine(raw)
        pictures.forEach {
            appendLine(it)
        }
        retweeted?.let { retweeted ->
            appendLine("==============================")
            appendLine("@${retweeted.username}")
            appendLine("时间: ${retweeted.createdAt}")
            appendLine("链接: ${retweeted.link}")
            appendLine(retweeted.raw)
            retweeted.pictures.forEach {
                appendLine(it)
            }
        }
    }

    private val status by lazy {
        LoginStatus(
            token = "ALT-MzYwMzU2NzkxMg==-1620944543-yf-615F189F8D8EAD8E32156E81A0ECF6A3-1",
            info = LoginUserInfo("用户3603567912", 3603567912),
            cookies = listOf(
                "tgc=TGT-MzYwMzU2NzkxMg==-1620944543-yf-230D154D9F8F06CDA26F1B7317DB2DF2-1; Domain=login.sina.com.cn; Path=/; Secure; HttpOnly; SameSite=None; \$x-enc=RAW",
                "SUB=_2A25NmdbPDeRhGeVI61EU9inFyj6IHXVu708HrDV_PUNbm9B-LU3hkW9NT_xCj1l-ZG38b9KNOwUh0x_l_ZqBXnC5; Domain=.sina.com.cn; Path=/; HttpOnly; \$x-enc=RAW",
                "SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WFE.yRYu2EwwyPDRUbJuMhh5JpX5KzhUgL.FoecehefSoM4eKz2dJLoI0MLxK-L1hnL1-zLxKBLBonL1KqLxKMLBK-L1--_i--fi-z7iKysi--NiK.XiKLsi--fiKyhiK.c; Domain=.sina.com.cn; Path=/; \$x-enc=RAW",
                "ALC=ac%3D2%26bt%3D1620944543%26cv%3D5.0%26et%3D1652480543%26ic%3D1972149112%26login_time%3D1620944543%26scf%3D%26uid%3D3603567912%26vf%3D0%26vs%3D1%26vt%3D0%26es%3Da816c5ee83897423454fdc7bab3a5255; Expires=Fri, 13 May 2022 22:22:23 GMT; Domain=login.sina.com.cn; Path=/; Secure; HttpOnly; SameSite=None; \$x-enc=RAW",
                "ALF=1652480543; Expires=Fri, 13 May 2022 22:22:23 GMT; Domain=.sina.com.cn; Path=/; \$x-enc=RAW",
                "LT=1620944543; Domain=login.sina.com.cn; Path=/; \$x-enc=RAW"
            )
        )
    }

    @Test
    fun flush(): Unit = runBlocking {
        val client = WeiboClient(status)
        client.flush().let {
            assertTrue(it.result)
            println(it)
        }
    }

    @Test
    fun qrcode() {
        // ALT-MzYwMzU2NzkxMg==-1620938651-yf-BC78F4DB4AD3C88EF47BF4486E1DD02E-1
        val client = WeiboClient()
        val login = GlobalScope.async {
            client.qrcode {
                qrcode.writeBytes(it)
            }
        }
        Runtime.getRuntime().exec("cmd /c ${qrcode.absolutePath}")
        runBlocking { login.await() }
        println(client.status())
    }

    @Test
    fun getUserMicroBlogsTest(): Unit = runBlocking {
        client.flush()
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
        client.flush()
        client.getUserHistory(uid = 6850282182).let {
            println(it)
        }
    }

    @Test
    fun getFeedGroupsTest(): Unit = runBlocking {
        client.flush()
        client.getFeedGroups().groups.forEach { group ->
            println("===${group.title}:${group.type}===")
            group.list.forEach { item ->
                println("${item.title}:${item.type} -> ${item.gid}")
            }
        }
    }

    @Test
    fun getTimelineTest(): Unit = runBlocking {
        client.flush()
        client.getTimeline(gid = 4056713441256071L, count = 100, type = TimelineType.GROUPS).statuses.forEach { blog ->
            blog.user?.let { client.getUserInfo(it.id) }
            println(blog.buildText())
        }
    }

    @Test
    fun getHotTest(): Unit = runBlocking {
        client.flush()
        client.getHot(gid = 102803L).statuses.forEach { blog ->
            blog.user?.let { client.getUserInfo(it.id) }
            println(blog.buildText())
        }
    }

    @Test
    fun getUserMentionsTest(): Unit = runBlocking {
        client.flush()
        client.getUserMentions()
    }
}