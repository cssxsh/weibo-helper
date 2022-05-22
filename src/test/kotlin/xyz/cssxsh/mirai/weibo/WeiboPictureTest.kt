package xyz.cssxsh.mirai.weibo

import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.yamlkt.Yaml
import org.junit.jupiter.api.Test
import xyz.cssxsh.mirai.weibo.data.WeiboHelperSettings

@OptIn(ConsoleExperimentalApi::class)
internal class WeiboPictureTest {


    @Test
    fun serializable() {
        // println(Json.encodeToString(WeiboPicture.serializer(), WeiboPicture.All()))
        println(Yaml.encodeToString(WeiboHelperSettings.updaterSerializer, Unit))
    }
}