package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value
import xyz.cssxsh.mirai.plugin.WeiboFilter

object WeiboHelperSettings : ReadOnlyPluginConfig("WeiboHelperSettings"), WeiboFilter {

    @ValueDescription("登录状态失效联系人")
    val contact by value(12345L)

    @ValueDescription("图片缓存位置")
    val cache: String by value("WeiboCache")

    @ValueDescription("图片缓存过期时间，单位小时，默认3天，为0时不会过期")
    val expire: Int by value(72)

    @ValueDescription("是否清理收藏的用户")
    val following: Boolean by value(true)

    @ValueDescription("快速轮询间隔，单位分钟")
    val fast: Int by value(1)

    @ValueDescription("慢速轮询间隔，单位分钟")
    val slow: Int by value(10)

    @ValueDescription("微博分组订阅器，转发数过滤器，默认16")
    override val repost: Long by value(16L)

    @ValueDescription("屏蔽的微博用户")
    override val users: Set<Long> by value(setOf(1191220232L))

    @ValueDescription("屏蔽的关键词正则表达式")
    @ValueName("regexes")
    private val regexes_: Set<String> by value(setOf("女拳"))

    @ValueDescription("屏蔽的关键词正则表达式")
    override val regexes: List<Regex> by lazy { regexes_.map { it.toRegex() } }

    @ValueDescription("关闭链接监听的群号")
    val quiet by value(emptySet<Long>())
}