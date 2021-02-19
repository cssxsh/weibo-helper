package xyz.cssxsh.weibo.data

import kotlinx.serialization.Serializable

@Serializable
enum class GenderType {
    /**
     * MALE
     */
    m,
    /**
     * FEMALE
     */
    f,
    /**
     * UNKNOWN
     */
    n;
}