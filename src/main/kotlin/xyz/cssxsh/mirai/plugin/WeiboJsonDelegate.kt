package xyz.cssxsh.mirai.plugin

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import xyz.cssxsh.weibo.WeiboClient
import xyz.cssxsh.weibo.data.MicroBlog
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class WeiboJsonDelegate(id: Long, type: String) : ReadWriteProperty<Any?, Map<Long, MicroBlog>> {
    private val file by lazy {
        data.resolve(type).resolve("$id.json").apply { parentFile.mkdirs() }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Map<Long, MicroBlog> {
        return runCatching {
            WeiboClient.Json.decodeFromString<Map<Long, MicroBlog>>(file.readText())
        }.recoverCatching {
            // 兼容性代码
            WeiboClient.Json.decodeFromString<List<MicroBlog>>(file.readText()).associateBy { it.id }
        }.getOrElse {
            emptyMap()
        }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Map<Long, MicroBlog>) {
        file.writeText(WeiboClient.Json.encodeToString(value))
    }
}