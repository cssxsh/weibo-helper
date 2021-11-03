plugins {
    kotlin("jvm") version Versions.kotlin
    kotlin("plugin.serialization") version Versions.kotlin

    id("net.mamoe.mirai-console") version  Versions.mirai
    id("net.mamoe.maven-central-publish") version "0.6.1"
}

group = "xyz.cssxsh"
version = "1.3.11"

mavenCentralPublish {
    useCentralS01()
    singleDevGithubProject("cssxsh", "weibo-helper")
    licenseFromGitHubProject("AGPL-3.0", "master")
    publication {
        artifact(tasks.getByName("buildPlugin"))
    }
}

repositories {
    clear()
    mavenLocal()
    maven(url = "https://maven.aliyun.com/repository/public")
    mavenCentral()
    maven(url = "https://maven.aliyun.com/repository/gradle-plugin")
    gradlePluginPortal()
}

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
//            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
//            languageSettings.useExperimentalAnnotation("io.ktor.util.KtorExperimentalAPI")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
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

    testImplementation("net.mamoe.yamlkt:yamlkt-jvm:0.10.2")
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
    compileKotlin {
        kotlinOptions.freeCompilerArgs += "-Xunrestricted-builder-inference"
    }
    test {
        useJUnitPlatform()
    }
}
