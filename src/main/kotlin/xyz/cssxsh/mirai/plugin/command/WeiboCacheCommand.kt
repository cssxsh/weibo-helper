package xyz.cssxsh.mirai.plugin.command

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.toPlainText
import xyz.cssxsh.mirai.plugin.*
import xyz.cssxsh.weibo.api.*
import java.time.YearMonth

object WeiboCacheCommand : CompositeCommand(
    owner = WeiboHelperPlugin,
    "wcache", "微博缓存",
    description = "微博缓存指令",
) {

    @SubCommand
    suspend fun CommandSenderOnMessage<*>.user(uid: Long, second: Int = 10, reposts: Int = 100) = sendMessage {
        val interval = second * 1000L
        val info = client.getUserInfo(uid).user
        val history = client.getUserHistory(uid)
        val months = history.flatMap { (year, months) -> months.map { YearMonth.of(year, it)!! } }.sortedDescending()
        launch {
            var count = 0
            months.forEach { month ->
                runCatching {
                    info.getRecord(month, interval).onEach { blog ->
                        if (blog.repostsCount >= reposts) blog.getImages(flush = false)
                    }
                }.onSuccess { record ->
                    count += record.size
                    if (record.isNotEmpty()) sendMessage("对@${info.screen}的${month}缓存下载完成, Total: ${record.size}")
                }.onFailure {
                    logger.warning("对@${info.screen}的${month}缓存下载失败", it)
                }
            }
            sendMessage("对@${info.screen}的缓存下载完成, ${count}/${info.statusesCount}")
        }
        "对@${info.screen}的{${months.first()}~${months.last()}}缓存任务开始".toPlainText()
    }

    @SubCommand
    suspend fun CommandSenderOnMessage<*>.group(gid: Long, second: Int = 10, reposts: Int = 100) = sendMessage {
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
            ImageCache.resolve("${info.id}").desktop(info)
            val history = client.getUserHistory(info.id)
            val months = history.flatMap { (year, months) ->
                months.map { YearMonth.of(year, it)!! }
            }.sortedDescending()
            var count = 0
            sendMessage("对@${info.screen}的{${months.first()}~${months.last()}}缓存任务开始")
            months.forEach { month ->
                runCatching {
                    info.getRecord(month, interval).onEach { blog ->
                        if (blog.repostsCount >= reposts) blog.getImages(flush = false)
                    }
                }.onSuccess { record ->
                    count += record.size
                    // if (record.isNotEmpty()) sendMessage("对@${info.screen}的${month}缓存下载完成, Total: ${record.size}")
                }.onFailure {
                    logger.warning("对@${info.screen}的${month}缓存下载失败", it)
                }
            }
            sendMessage("对@${info.screen}的缓存下载完成, ${count}/${info.statusesCount}")
        }
        "对Group($gid)的缓存文件夹图标已设置".toPlainText()
    }
}