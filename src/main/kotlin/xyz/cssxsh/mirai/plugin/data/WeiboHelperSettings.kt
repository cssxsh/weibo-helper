package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object WeiboHelperSettings : ReadOnlyPluginConfig("WeiboHelperSettings") {
    @ValueDescription("图片缓存位置")
    val cache: String by value("WeiboCache")

    @ValueDescription("图片缓存过期时间，单位小时，默认3天，为0时不会过期")
    val expire: Int by value(72)

    @ValueDescription("快速轮询间隔，单位分钟")
    val fast: Int by value(1)

    @ValueDescription("慢速轮询间隔，单位分钟")
    val slow: Int by value(10)
}