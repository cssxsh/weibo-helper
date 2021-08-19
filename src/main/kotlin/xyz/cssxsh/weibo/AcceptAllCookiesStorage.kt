package xyz.cssxsh.weibo

import io.ktor.client.features.cookies.*
import io.ktor.http.*
import kotlinx.coroutines.sync.*
import kotlin.properties.*

private inline fun <reified T : Any, reified R> reflect() = ReadOnlyProperty<T, R> { thisRef, property ->
    thisRef::class.java.getDeclaredField(property.name).apply { isAccessible = true }.get(thisRef) as R
}

internal val AcceptAllCookiesStorage.mutex: Mutex by reflect()

internal val AcceptAllCookiesStorage.container: MutableList<Cookie> by reflect()