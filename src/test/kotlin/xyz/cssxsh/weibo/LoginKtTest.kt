package xyz.cssxsh.weibo

import kotlinx.coroutines.*
import net.mamoe.mirai.console.util.*
import org.junit.jupiter.api.*
import xyz.cssxsh.weibo.api.*
import javax.imageio.*

internal class LoginKtTest : WeiboClientTest() {

    @Test
    fun flush(): Unit = runBlocking {
        client.restore()
        println(client.status().info)
    }

    @Test
    fun qrcode(): Unit = runBlocking {
        client.qrcode { url ->
            println(url)
        }
        println(client.status().info)
    }

    @Test
    fun code() {
        val image = ImageIO.read(qrcode)
        val message = buildAnsiMessage {
            for (y in 4 until 175 step 3) {
                for (x in 4 until 175 step 3) {
                    val rgb = image.getRGB(x, y)
                    if (rgb != -1) ansi("\u001b[40m")
                    append('　')
                    reset()
                }
                appendLine()
            }
        }
        println(message)
    }

    @Test
    fun incarnate(): Unit = runBlocking {
        client.incarnate()
    }
}