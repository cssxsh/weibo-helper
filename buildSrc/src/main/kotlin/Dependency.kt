@file:Suppress("unused")

import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.kotlinx(module: String, version: String = Versions.kotlin) =
    "org.jetbrains.kotlinx:kotlinx-$module:$version"

fun DependencyHandler.ktor(module: String, version: String= Versions.ktor) =
    "io.ktor:ktor-$module:$version"

fun DependencyHandler.mirai(module: String, version: String = "+") =
    "net.mamoe:mirai-$module:$version"

fun DependencyHandler.jsoup(version: String = Versions.jsoup) =
    "org.jsoup:jsoup:$version"
