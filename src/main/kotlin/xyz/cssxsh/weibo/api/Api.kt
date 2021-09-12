package xyz.cssxsh.weibo.api

const val INDEX_PAGE = "https://weibo.com"
const val LOGIN_PAGE = "https://weibo.com/login.php"

const val PAGE_SIZE = 20

// STATUSES
const val STATUSES_CONFIG = "https://weibo.com/ajax/statuses/config"
const val STATUSES_MY_MICRO_BLOG = "https://weibo.com/ajax/statuses/mymblog"
const val STATUSES_SHOW = "https://weibo.com/ajax/statuses/show"
const val STATUSES_LONGTEXT = "https://weibo.com/ajax/statuses/longtext"
const val STATUSES_MENTIONS = "https://weibo.com/ajax/statuses/mentions"
const val STATUSES_REPOST = "https://weibo.com/ajax/statuses/repostTimeline"
const val STATUSES_LIKE_LIST = "https://weibo.com/ajax/statuses/likelist?uid=3603567912&page=1"
const val STATUSES_LIKE_SHOW = "https://weibo.com/ajax/statuses/likeShow"
const val STATUSES_FAVORITES = "https://weibo.com/ajax/favorites/all_fav?uid=3603567912&page=1"
const val SEARCH_ALL = "https://weibo.com/ajax/search/all"

// COMMENTS
const val COMMENTS_MENTIONS = "https://weibo.com/ajax/comments/mentions"

// MESSAGE
const val MESSAGE_CMT = "https://weibo.com/ajax/message/cmt"
const val MESSAGE_ATTITUDES = "https://weibo.com/ajax/message/attitudes"
const val MESSAGE_WHITELIST = "https://weibo.com/ajax/message/whitelist"

// FEED
const val FEED_ALL_GROUPS = "https://weibo.com/ajax/feed/allGroups"
const val FEED_UNREAD_FRIENDS_TIMELINE = "https://weibo.com/ajax/feed/unreadfriendstimeline"
const val FEED_FRIENDS_TIMELINE = "https://weibo.com/ajax/feed/friendstimeline"
const val FEED_GROUPS_TIMELINE = "https://weibo.com/ajax/feed/groupstimeline"
const val FEED_HOT_TIMELINE = "https://weibo.com/ajax/feed/hottimeline"

// FRIENDSHIPS
const val FRIENDSHIPS = "https://weibo.com/ajax/friendships/friends?page=1&uid=3603567912"
const val ss = "https://weibo.com/ajax/friendships/friends?uid=3603567912&relate=fans&count=20&fansSortType=fansCount"
const val FRIENDSHIPS_CREATE = "https://weibo.com/ajax/friendships/create"

// PROFILE
const val PROFILE_INFO = "https://weibo.com/ajax/profile/info"
const val PROFILE_DETAIL = "https://weibo.com/ajax/profile/detail"
const val PROFILE_HISTORY = "https://weibo.com/ajax/profile/mbloghistory"
const val PROFILE_MY_HOT = "https://weibo.com/ajax/profile/myhot"
const val PROFILE_SEARCH = "https://weibo.com/ajax/profile/searchblog?uid=5832199024&page=1&feature=0&q=ss"
const val PROFILE_FEATURE_DETAIL = "https://weibo.com/ajax/profile/featuredetail?uid=2353979124"
const val PROFILE_VIDEO = "https://weibo.com/ajax/profile/getprofilevideolist?uid=2353979124&cursor=0"
const val PROFILE_TINY_VIDEO = "https://weibo.com/ajax/profile/gettinyvideo?uid=2353979124&cursor=0&count=20"
const val PROFILE_IMAGE = "https://weibo.com/ajax/profile/getImageWall?uid=2353979124&sinceid=0&has_album=true"
const val PROFILE_TAB_LIST = "https://weibo.com/ajax/profile/tablist?uid=2353979124"
const val PROFILE_GROUP_MEMBERS = "https://weibo.com/ajax/profile/getGroupMembers"
// sortType=timeDown, sortType=null
const val PROFILE_FOLLOW_CONTENT = "https://weibo.com/ajax/profile/followContent?sortType=all"
const val PROFILE_TOPIC_CONTENT = "https://weibo.com/ajax/profile/topicContent?tabid=231093_-_recently"
const val PROFILE_GROUP_LIST = "https://weibo.com/ajax/profile/getGroupList"
const val PROFILE_GROUP_SET = "https://weibo.com/ajax/profile/setGroup"
const val PROFILE_GROUPS = "https://weibo.com/ajax/profile/getGroups?target_uid=6077799204&filterType=system&hasRecom=true"

// LOGIN
const val CROSS_DOMAIN = "https://login.sina.com.cn/crossdomain2.php"
const val WEIBO_SSO_LOGIN = "https://passport.weibo.com/wbsso/login"
const val SSO_LOGIN = "https://login.sina.com.cn/sso/login.php"
const val SSO_QRCODE_IMAGE = "https://login.sina.com.cn/sso/qrcode/image"
const val SSO_QRCODE_CHECK = "https://login.sina.com.cn/sso/qrcode/check"
const val PASSPORT_VISITOR = "https://passport.weibo.com/visitor/visitor"
const val PASSPORT_GEN_VISITOR = "https://passport.weibo.com/visitor/genvisitor"