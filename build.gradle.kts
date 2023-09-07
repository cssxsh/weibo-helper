plugins {
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.serialization") version "1.8.22"

    id("net.mamoe.mirai-console") version "2.15.0"
    id("me.him188.maven-central-publish") version "1.0.0-dev-3"
}

group = "xyz.cssxsh"
version = "1.6.2"

mavenCentralPublish {
    useCentralS01()
    singleDevGithubProject("cssxsh", "weibo-helper")
    licenseFromGitHubProject("AGPL-3.0")
    workingDir = System.getenv("PUBLICATION_TEMP")?.let { file(it).resolve(projectName) }
        ?: buildDir.resolve("publishing-tmp")
    publication {
        artifact(tasks["buildPlugin"])
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.jclarion:image4j:0.7")
    implementation("org.apache.commons:commons-text:1.10.0")
    implementation("org.jsoup:jsoup:1.16.1")
    testImplementation(kotlin("test"))
    testImplementation("net.mamoe.yamlkt:yamlkt-jvm:0.10.2")
    //
    implementation(platform("net.mamoe:mirai-bom:2.14.0"))
    compileOnly("net.mamoe:mirai-console-compiler-common")
    testImplementation("net.mamoe:mirai-logging-slf4j")
    //
    implementation(platform("io.ktor:ktor-bom:2.3.4"))
    implementation("io.ktor:ktor-client-okhttp")
    implementation("io.ktor:ktor-client-encoding")
    //
    implementation(platform("org.slf4j:slf4j-parent:2.0.7"))
    testImplementation("org.slf4j:slf4j-simple")
}

kotlin {
    explicitApi()
}

tasks {
    test {
        useJUnitPlatform()
    }
}
