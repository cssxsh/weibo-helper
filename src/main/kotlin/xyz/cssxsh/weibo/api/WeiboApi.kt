package xyz.cssxsh.weibo.api

object WeiboApi {
    const val INDEX_PAGE = "https://weibo.com"
    // STATUSES
    const val STATUSES_PAGE_SIZE = 20
    const val STATUSES_MY_MICRO_BLOG = "https://weibo.com/ajax/statuses/mymblog"
    const val STATUSES_SHOW = "https://weibo.com/ajax/statuses/show"
    const val STATUSES_LONGTEXT = "https://weibo.com/ajax/statuses/longtext"
    const val STATUSES_EXTEND = "https://m.weibo.cn/statuses/extend"
    const val STATUSES_MENTIONS = "https://weibo.com/ajax/statuses/mentions"
    const val STATUSES_REPOST = "https://weibo.com/ajax/statuses/repostTimeline"
    const val STATUSES_LIKE_LIST = "https://weibo.com/ajax/statuses/likelist"
    const val STATUSES_LIKE_SHOW = "https://weibo.com/ajax/statuses/likeShow"
    const val STATUSES_FAVORITES = "https://weibo.com/ajax/favorites/all_fav"
    // FEED
    const val FEED_ALL_GROUPS = "https://weibo.com/ajax/feed/allGroups"
    const val FEED_UNREAD_FRIENDS_TIMELINE = "https://weibo.com/ajax/feed/unreadfriendstimeline"
    const val FEED_FRIENDS_TIMELINE = "https://weibo.com/ajax/feed/friendstimeline"
    const val FEED_GROUPS_TIMELINE = "https://weibo.com/ajax/feed/groupstimeline"
    const val FEED_HOT_TIMELINE = "https://weibo.com/ajax/feed/hottimeline"
    // PROFILE
    const val PROFILE_INFO = "https://weibo.com/ajax/profile/info"
    const val PROFILE_DETAIL = "https://weibo.com/ajax/profile/detail"
    const val PROFILE_HISTORY = "https://weibo.com/ajax/profile/mbloghistory"
    // LOGIN
    const val SINA_LOGIN = "https://login.sina.com.cn/sso/login.php"
    const val CROSS_DOMAIN = "https://login.sina.com.cn/crossdomain2.php"
    const val SSO_LOGIN = "https://passport.weibo.com/wbsso/login"
}