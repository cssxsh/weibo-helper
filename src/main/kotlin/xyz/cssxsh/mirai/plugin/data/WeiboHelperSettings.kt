package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value
import xyz.cssxsh.weibo.*

object WeiboHelperSettings : ReadOnlyPluginConfig("WeiboHelperSettings") {
    /**
     * 图片缓存位置
     */
    @ValueName("cache_path")
    val cachePath: String by value("WeiboCache")

    @ValueName("init_cookies")
    val initCookies: List<HttpCookie> by value(emptyList())
}