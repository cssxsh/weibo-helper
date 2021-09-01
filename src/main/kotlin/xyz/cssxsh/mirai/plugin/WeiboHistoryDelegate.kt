package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import kotlinx.serialization.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.data.*
import java.time.*
import kotlin.properties.*
import kotlin.reflect.*

class WeiboHistoryDelegate<K : Comparable<K>>(id: K, subscriber: WeiboSubscriber<K>) :
    ReadWriteProperty<Any?, Map<Long, MicroBlog>> {
    private val file = data.resolve(subscriber.type).resolve("$id.json").apply { parentFile.mkdirs() }

    private lateinit var map: Map<Long, MicroBlog>

    init {
        runCatching {
            map = WeiboClient.Json.decodeFromString(file.readText().ifBlank { """{}""" })
        }.onFailure {
            logger.warning { "${file.absolutePath} 读取失败" }
            map = emptyMap()
        }
        subscriber.launch(SupervisorJob()) {
            while (isActive) {
                delay(IntervalSlow.toMillis())
                synchronized(file) {
                    runCatching {
                        file.writeText(WeiboClient.Json.encodeToString(map))
                    }
                }
            }
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Map<Long, MicroBlog> = synchronized(file) { map }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Map<Long, MicroBlog>) = synchronized(file) {
        val expire = OffsetDateTime.now().minusDays(HistoryExpire)
        map = value.filterValues { blog -> blog.created > expire }
    }
}