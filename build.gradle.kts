plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"

    id("net.mamoe.mirai-console") version  "2.11.0"
    id("net.mamoe.maven-central-publish") version "0.7.1"
}

group = "xyz.cssxsh"
version = "1.4.16"

mavenCentralPublish {
    useCentralS01()
    singleDevGithubProject("cssxsh", "weibo-helper")
    licenseFromGitHubProject("AGPL-3.0", "master")
    publication {
        artifact(tasks.getByName("buildPlugin"))
        artifact(tasks.getByName("buildPluginLegacy"))
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-encoding:1.6.7") {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.jetbrains.kotlinx")
        exclude(group = "org.slf4j")
        exclude(group = "io.ktor", module = "ktor-client-core")
    }
    implementation("org.jclarion:image4j:0.7")
    implementation("org.apache.commons:commons-text:1.9")
    compileOnly("net.mamoe:mirai-core-utils:2.11.0")

    testImplementation("net.mamoe.yamlkt:yamlkt-jvm:0.10.2")
    testImplementation(kotlin("test", "1.6.21"))
}

tasks {
    compileKotlin {
        kotlinOptions.freeCompilerArgs += "-Xunrestricted-builder-inference"
    }
    test {
        useJUnitPlatform()
    }
}
