package xyz.cssxsh.weibo.api

object WeiboApi {
    const val INDEX_PAGE = "https://weibo.com"
    // BLOG
    const val STATUSES_PAGE_SIZE = 20
    const val STATUSES_MY_MICRO_BLOG = "https://weibo.com/ajax/statuses/mymblog"
    const val STATUSES_SHOW = "https://weibo.com/ajax/statuses/show"
    const val STATUSES_LONGTEXT = "https://weibo.com/ajax/statuses/longtext"
    // FEED
    const val ALL_GROUPS = "https://weibo.com/ajax/feed/allGroups"
    const val UNREAD_FRIENDS_TIMELINE = "https://weibo.com/ajax/feed/unreadfriendstimeline"
    const val FRIENDS_TIMELINE = "https://weibo.com/ajax/feed/friendstimeline"
    const val GROUPS_TIMELINE = "https://weibo.com/ajax/feed/groupstimeline"
    // PROFILE
    const val PROFILE_INFO = "https://weibo.com/ajax/profile/info"
    // LOGIN
    const val SINA_LOGIN = "https://login.sina.com.cn/sso/login.php"
    const val CROSS_DOMAIN = "https://login.sina.com.cn/crossdomain2.php"
    const val SSO_LOGIN = "https://passport.weibo.com/wbsso/login"
}