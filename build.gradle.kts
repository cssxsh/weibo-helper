plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.serialization") version "1.6.0"

    id("net.mamoe.mirai-console") version  "2.10.0"
    id("net.mamoe.maven-central-publish") version "0.7.1"
}

group = "xyz.cssxsh"
version = "1.4.11"

mavenCentralPublish {
    useCentralS01()
    singleDevGithubProject("cssxsh", "weibo-helper")
    licenseFromGitHubProject("AGPL-3.0", "master")
    publication {
        artifact(tasks.getByName("buildPlugin"))
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
    }
}

dependencies {
    // implementation(ktor("client-serialization", Versions.ktor))
    implementation("io.ktor:ktor-client-encoding:1.6.5") {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.jetbrains.kotlinx")
        exclude(group = "org.slf4j")
        exclude(group = "io.ktor", module = "ktor-client-core")
    }
    implementation("org.jclarion:image4j:0.7")
    implementation("org.apache.commons:commons-text:1.9")
    compileOnly("net.mamoe:mirai-core-utils:2.10.0")

    testImplementation("net.mamoe.yamlkt:yamlkt-jvm:0.10.2")
    testImplementation(kotlin("test", "1.6.0"))
}

mirai {
    configureShadow {
        exclude("module-info.class")
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
