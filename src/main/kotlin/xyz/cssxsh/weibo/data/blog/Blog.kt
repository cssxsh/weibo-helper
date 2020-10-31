package xyz.cssxsh.weibo.data.blog

interface Blog {
    val id: String
    val user: User
    val rawText: String
    val pics: List<Pic>
}