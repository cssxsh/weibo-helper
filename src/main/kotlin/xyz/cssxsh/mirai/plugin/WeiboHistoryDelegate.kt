package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import kotlinx.serialization.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.data.*
import java.time.*
import kotlin.properties.*
import kotlin.reflect.*

@OptIn(ExperimentalSerializationApi::class)
class WeiboHistoryDelegate<K : Comparable<K>>(id: K, subscriber: WeiboSubscriber<K>) :
    ReadOnlyProperty<Any?, MutableMap<Long, MicroBlog>> {
    private val file = DataFolder.resolve(subscriber.type).resolve("$id.json").apply { parentFile.mkdirs() }

    private var cache: MutableMap<Long, MicroBlog> = HashMap()

    init {
        try {
            cache.putAll(WeiboClient.Json.decodeFromString(file.readText().ifBlank { """{}""" }))
        } catch (e: Throwable) {
            logger.warning { "${file.absolutePath} 读取失败" }
        }
        subscriber.launch(SupervisorJob()) {
            while (isActive) {
                delay(IntervalSlow.toMillis())
                try {
                    val expire = OffsetDateTime.now().minusDays(HistoryExpire)
                    file.writeText(WeiboClient.Json.encodeToString(cache.filterValues { blog -> blog.created > expire }))
                } catch (e: Throwable) {
                    logger.warning({ "${file.absolutePath} 保存失败" }, e)
                }
            }
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): MutableMap<Long, MicroBlog> = synchronized(file) {
        cache
    }
}