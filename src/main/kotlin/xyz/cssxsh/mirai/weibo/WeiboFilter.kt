package xyz.cssxsh.mirai.weibo

interface WeiboFilter {
    val repost: Long
    val users: Set<Long>
    val regexes: List<Regex>
    val urls: Set<Int>
}