package xyz.cssxsh.mirai.plugin

interface WeiboFilter {
    val repost: Long
    val users: Set<Long>
    val regexes: List<Regex>
    val urls: Set<Int>
}