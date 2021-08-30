package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import kotlinx.serialization.*
import xyz.cssxsh.weibo.*
import xyz.cssxsh.weibo.data.*
import java.time.*
import kotlin.properties.*
import kotlin.reflect.*

class WeiboHistoryDelegate<K : Comparable<K>>(id: K, type: String, scope: CoroutineScope = MainScope()) :
    ReadWriteProperty<Any?, Map<Long, MicroBlog>> {
    private val file = data.resolve(type).resolve("$id.json").apply { parentFile.mkdirs() }

    private var map: Map<Long, MicroBlog> = emptyMap()

    init {
        scope.launch(SupervisorJob()) {
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

    override fun getValue(thisRef: Any?, property: KProperty<*>): Map<Long, MicroBlog> {
        return runCatching {
            synchronized(file) {
                map.ifEmpty { WeiboClient.Json.decodeFromString(file.readText()) }
            }
        }.getOrElse {
            emptyMap()
        }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Map<Long, MicroBlog>) {
        synchronized(file) {
            val expire = OffsetDateTime.now().minusDays(HistoryExpire)
            map = value.filterValues { blog -> blog.created.isAfter(expire) }
        }
    }
}