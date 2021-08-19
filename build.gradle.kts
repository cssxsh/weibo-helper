plugins {
    kotlin("jvm") version Versions.kotlin
    kotlin("plugin.serialization") version Versions.kotlin

    id("net.mamoe.mirai-console") version  Versions.mirai
}

group = "xyz.cssxsh.mirai.plugin"
version = "1.1.4"

repositories {
    clear()
    mavenLocal()
    maven(url = "https://maven.aliyun.com/repository/public")
    mavenCentral()
    jcenter()
    maven(url = "https://maven.aliyun.com/repository/gradle-plugin")
    gradlePluginPortal()
}

kotlin {
    sourceSets {
        all {
//            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
//            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
//            languageSettings.useExperimentalAnnotation("io.ktor.util.KtorExperimentalAPI")
//            languageSettings.useExperimentalAnnotation("kotlinx.serialization.ExperimentalSerializationApi")
//            languageSettings.useExperimentalAnnotation("kotlinx.serialization.InternalSerializationApi")
//            languageSettings.useExperimentalAnnotation("net.mamoe.mirai.console.util.ConsoleExperimentalApi")
        }
        test {
//            languageSettings.useExperimentalAnnotation("net.mamoe.mirai.console.ConsoleFrontEndImplementation")
        }
    }
}

dependencies {
    // implementation(ktor("client-serialization", Versions.ktor))
    implementation(ktor("client-encoding", Versions.ktor)) {
        exclude(group = "io.ktor", module = "ktor-client-core")
    }
    implementation("org.jclarion:image4j:0.7")
    implementation("org.apache.commons:commons-text:1.9")

    testImplementation("net.mamoe.yamlkt:yamlkt-jvm:0.9.0")
    testImplementation(kotlin("test-junit"))
    testImplementation(junit("api", Versions.junit))
    testRuntimeOnly(junit("engine", Versions.junit))
}

mirai {
    configureShadow {
        exclude {
            it.path.startsWith("kotlin")
        }
        exclude {
            it.path.startsWith("org/intellij")
        }
        exclude {
            it.path.startsWith("org/jetbrains")
        }
        exclude {
            it.path.startsWith("org/slf4j")
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}
