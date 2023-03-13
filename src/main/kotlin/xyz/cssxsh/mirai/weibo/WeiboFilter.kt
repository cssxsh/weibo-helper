package xyz.cssxsh.mirai.weibo

public interface WeiboFilter {
    public val repost: Long
    public val users: Set<Long>
    public val regexes: List<Regex>
    public val urls: Set<Int>
    public val original: Boolean
    public val likes: Boolean
}