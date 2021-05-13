package xyz.cssxsh.mirai.plugin.data

import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value
import xyz.cssxsh.weibo.*

object WeiboHelperSettings : ReadOnlyPluginConfig("WeiboHelperSettings") {
    @ValueDescription("图片缓存位置")
    val cache: String by value("WeiboCache")

    @ValueName("json格式的Cookie文件 导出工具 https://www.editthiscookie.com/")
    val cookies: String by value("cookies.json")
}