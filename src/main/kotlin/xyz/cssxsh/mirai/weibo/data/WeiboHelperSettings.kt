package xyz.cssxsh.mirai.weibo.data

import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.data.SerializableValue.Companion.serializableValueWith
import net.mamoe.mirai.console.internal.data.*
import xyz.cssxsh.mirai.weibo.*

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
    override val regexes: List<Regex> by lazy { regexes_.map { it.toRegex() } }

    @ValueDescription("屏蔽URL类型，填入 39 可以屏蔽微博视频")
    override val urls: Set<Int> by value()

    @ValueDescription("发送微博视频文件")
    val video: Boolean by value(true)

    @ValueDescription("处理微博表情")
    val emoticon: Boolean by value(true)

    @ValueDescription("显示图片数设置")
    @Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
    val picture: WeiboPicture by LazyReferenceValueImpl<WeiboPicture>()
        .serializableValueWith(WeiboPicture.serializer())
        .apply { value = WeiboPicture.All() }

    @ValueDescription("显示封面设置")
    val cover: Boolean by value(true)

    @ValueDescription("历史记录保留时间，单位天，默认 7d")
    val history by value(7L)

    @ValueDescription("Http 超时时间")
    val timeout by value(60_000L)

    @ValueDescription("以转发消息的方式发送订阅微博")
    val forward: Boolean by value(false)

    @ValueName("show_url")
    @ValueDescription("是否显示url")
    val showUrl: Boolean by value(true)
}