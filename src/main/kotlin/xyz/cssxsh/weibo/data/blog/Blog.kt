package xyz.cssxsh.weibo.data.blog

interface Blog {
    val mBlogType: Int
    val id: String
    val user: User
    val createdAt: String
    val rawText: String
    val pics: List<Pic>
}