package xyz.cssxsh.mirai.plugin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.io.File

internal class WeiboUtilsKtTest {

    private fun String.getUser() = substring(0..7).let {
        if (it.startsWith("00")) it.toLong62() else it.toLong(16)
    }

    private val cache = File("F:\\WeiboCache")

    private val REGEX = """\d+-\d+-\d+""".toRegex()

    @Test
    fun getUser() {
        cache.listFiles { file ->
            file.name.matches(REGEX)
        }?.forEach { dir ->
            dir.listFiles()?.forEach { image ->
                println(image.name.substring(19))
                val user = image.name.replace("""\d+-\d+-""".toRegex(), "").getUser()
                val dest = cache.resolve("$user").resolve(image.name).apply { parentFile.mkdirs() }
                image.renameTo(dest)
            }
            dir.delete()
        }
    }

    @Test
    fun timestamp() {
        cache.listFiles{ file ->
            file.name.matches("""\d+""".toRegex())
        }?.forEach { dir ->
/*            dir.listFiles()?.forEach { image ->
                val t = Instant.ofEpochSecond(timestamp(image.name.substring(0..15).toLong())).atOffset(OffsetTime.now().offset)
                val dest = cache.resolve(t.format(DateTimeFormatter.ISO_LOCAL_DATE)).resolve(image.name).apply {
                    parentFile.mkdirs()
                }
                image.renameTo(dest)
            }
            dir.delete()*/
            dir.listFiles()?.forEach { image ->
                val dest = cache.resolve(image.name)
                image.listFiles()?.forEach { file ->
                    file.renameTo(dest.resolve(file.name))
                }
                image.delete()
            }
            dir.delete()
        }
    }
}