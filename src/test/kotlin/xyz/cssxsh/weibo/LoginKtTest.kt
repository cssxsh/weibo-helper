package xyz.cssxsh.weibo

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import xyz.cssxsh.weibo.api.*

internal class LoginKtTest: WeiboClientTest() {

    @Test
    fun flush(): Unit = runBlocking {
        client.restore()
    }

    @Test
    fun qrcode(): Unit = runBlocking {
        val channel = Channel<ByteArray>()
        val job = launch {
            client.qrcode(channel::send)
        }
        qrcode.writeBytes(channel.receive())
        Runtime.getRuntime().exec("cmd /c ${qrcode.absolutePath}")
        job.join()
        status = client.status()
        println(client.status().info)
    }

    @Test
    fun incarnate(): Unit = runBlocking {
        client.incarnate()
    }
}