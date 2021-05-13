@file:Suppress("UnstableApiUsage")

pluginManagement {
    plugins {
        kotlin("jvm") version "1.4.30"
        kotlin("plugin.serialization") version "1.4.30"

        id("net.mamoe.mirai-console") version "2.6.4"
    }
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven(url = "https://maven.aliyun.com/repository/releases")
        maven(url = "https://mirrors.huaweicloud.com/repository/maven")
        // bintray dl.bintray.com -> bintray.proxy.ustclug.org
        maven(url = "https://bintray.proxy.ustclug.org/him188moe/mirai/")
        maven(url = "https://bintray.proxy.ustclug.org/kotlin/kotlin-dev")
        maven(url = "https://bintray.proxy.ustclug.org/kotlin/kotlinx/")
        // central
        maven(url = "https://maven.aliyun.com/repository/central")
        mavenCentral()
        // jcenter
        maven(url = "https://maven.aliyun.com/repository/jcenter")
        jcenter()
    }
}
rootProject.name = "weibo-helper"

include("tools")

