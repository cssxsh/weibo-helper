package xyz.cssxsh.mirai.weibo.command

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.weibo.*
import xyz.cssxsh.weibo.api.*
import java.time.*

object WeiboCacheCommand : CompositeCommand(
    owner = WeiboHelperPlugin,
    "wcache", "微博缓存",
    description = "微博缓存指令",
) {

    @SubCommand
    suspend fun CommandSenderOnMessage<*>.user(uid: Long, second: Int = 10, reposts: Int = 100) = quote {
        val interval = second * 1000L
        val info = client.getUserInfo(uid).user
        val history = client.getUserHistory(uid)
        val months = history.flatMap { (year, months) -> months.map { YearMonth.of(year, it)!! } }.sortedDescending()
        launch {
            var count = 0
            for (month in months) {
                runCatching {
                    info.getRecord(month, interval).onEach { blog ->
                        if (blog.reposts >= reposts) blog.getImages(flush = false).awaitAll()
                    }
                }.onSuccess { record ->
                    count += record.size
                }.onFailure {
                    logger.warning({ "对@${info.screen}的${month}缓存下载失败" }, it)
                }
            }
            sendMessage("对@${info.screen}的缓存下载完成, ${count}/${info.statuses}")
        }
        "对@${info.screen}的{${months.first()}~${months.last()}}缓存任务开始".toPlainText()
    }

    @SubCommand
    suspend fun CommandSenderOnMessage<*>.group(gid: Long, second: Int = 10, reposts: Int = 100) = quote {
        val interval = second * 1000L
        val members = flow {
            var page = 1
            while (currentCoroutineContext().isActive) {
                runCatching {
                    client.getGroupMembers(gid = gid, page = page++)
                }.onSuccess {
                    if (it.users.isEmpty()) return@flow
                    emitAll(it.users.asFlow())
                }.onFailure {
                    logger.info(it.message)
                }.getOrNull() ?: break
            }
        }
        members.collect { info ->
            val history = client.getUserHistory(info.id)
            val months = history.flatMap { (year, months) ->
                months.map { YearMonth.of(year, it)!! }
            }.sortedDescending()
            var count = 0
            sendMessage("对@${info.screen}的{${months.first()}~${months.last()}}缓存任务开始")
            for (month in months) {
                runCatching {
                    info.getRecord(month, interval).onEach { blog ->
                        if (blog.reposts >= reposts) blog.getImages(flush = false).awaitAll()
                    }
                }.onSuccess { record ->
                    count += record.size
                }.onFailure {
                    logger.warning({ "对@${info.screen}的${month}缓存下载失败" }, it)
                }
            }
            sendMessage("对@${info.screen}的缓存下载完成, ${count}/${info.statuses}")
        }
        "对Group($gid)的缓存文件夹图标已设置".toPlainText()
    }

    @SubCommand
    suspend fun CommandSenderOnMessage<*>.clean(following: Boolean, num: Int) = quote {
        ImageCache.clean(following, num)
        "清理完成".toPlainText()
    }

    @SubCommand
    suspend fun CommandSenderOnMessage<*>.emoticon() = quote {
        Emoticons.values.onEach { emoticon ->
            try {
                emoticon.file()
            } catch (cause: Exception) {
                logger.warning({ "表情${emoticon.phrase} 下载失败 ${emoticon.url}" }, cause)
            }
        }.joinTo(MessageChainBuilder()) { info ->
            "${info.category.ifBlank { "默认" }}/${info.phrase}"
        }.build()
    }
}