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
    private val file = DataFolder.resolve(subscriber.type).resolve("$id.json")

    private val cache: MutableMap<Long, MicroBlog> = HashMap()

    init {
        try {
            if (file.exists()) {
                cache.putAll(WeiboClient.Json.decodeFromString(file.readText().ifBlank { """{}""" }))
            } else {
                file.parentFile.mkdirs()
                file.writeText("{}")
            }
        } catch (e: Throwable) {
            logger.warning({ "${file.absolutePath} 读取失败" }, e)
        }
        subscriber.launch(SupervisorJob()) {
            while (isActive) {
                delay(IntervalSlow.toMillis())
                save()
            }
        }.invokeOnCompletion {
            logger.info { "WeiboHistory ${file.absolutePath} 已保存 " }
            save()
        }
    }

    private fun save() {
        try {
            val write = if (cache.size > 8196) {
                val expire = OffsetDateTime.now().minusDays(HistoryExpire)
                cache.filterValues { blog -> blog.created > expire }
            } else {
                cache
            }
            file.writeText(WeiboClient.Json.encodeToString(write))
        } catch (e: Throwable) {
            logger.warning({ "WeiboHistory ${file.absolutePath} 保存失败" }, e)
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): MutableMap<Long, MicroBlog> = cache
}