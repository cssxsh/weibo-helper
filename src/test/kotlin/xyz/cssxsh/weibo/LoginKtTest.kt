package xyz.cssxsh.weibo

import kotlinx.coroutines.channels.*
import kotlinx.coroutines.*
import org.junit.jupiter.api.*
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
        println(client.status().info)
    }

    @Test
    fun incarnate(): Unit = runBlocking {
        client.incarnate()
    }
}