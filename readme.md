# [Weibo Helper](https://github.com/cssxsh/weibo-helper)

> 基于 [Mirai Console](https://github.com/mamoe/mirai-console) 的 [微博](https://weibo.com/) 转发插件

[![Release](https://img.shields.io/github/v/release/cssxsh/weibo-helper)](https://github.com/cssxsh/weibo-helper/releases)
[![Downloads](https://img.shields.io/github/downloads/cssxsh/weibo-helper/total)](https://shields.io/category/downloads)
[![MiraiForum](https://img.shields.io/badge/post-on%20MiraiForum-yellow)](https://mirai.mamoe.net/topic/212)

**使用前应该查阅的相关文档或项目**

* [User Manual](https://github.com/mamoe/mirai/blob/dev/docs/UserManual.md)
* [Permission Command](https://github.com/mamoe/mirai/blob/dev/mirai-console/docs/BuiltInCommands.md#permissioncommand)
* [Chat Command](https://github.com/project-mirai/chat-command)

插件基于PC网页版微博API，使用插件需要[登录](#登录指令)一个微博账号   
插件初始化时，如果恢复登录状态失败，则会尝试模拟游客

## 指令

注意: 使用前请确保可以 [在聊天环境执行指令](https://github.com/project-mirai/chat-command)  
带括号的 `/` 前缀是可选的  
`<...>` 中的是指令名，由空格隔开表示或，选择其中任一名称都可执行例如 `/微博用户 订阅`  
`[...]` 表示参数，当 `[...]` 后面带 `?` 时表示参数可选  
`{...}` 表示连续的多个参数

本插件指令权限ID 格式为 `xyz.cssxsh.mirai.plugin.weibo-helper:command.*`, `*` 是指令的第一指令名  
例如 `/微博用户 订阅` 的权限ID为 `xyz.cssxsh.mirai.plugin.weibo-helper:command.wuser`

`[subject]?`订阅的接收对象，可选参数, 默认填充当前聊天环境

### 登录指令

| 指令               | 描述       |
|:-----------------|:---------|
| `/<wlogin 微博登录>` | 登录一个微博账号 |

使用指令后，机器人会发送网页登录的二维码  
使用手机微博APP扫描确认登录后  
如果成功登录，则会回复 `@用户名#ID 登陆成功` 的消息

### 用户订阅指令

| 指令                                              | 描述         |
|:------------------------------------------------|:-----------|
| `/<wuser 微博用户> <add, task 订阅> [uid] [subject]?` | 订阅一个微博账号   |
| `/<wuser 微博用户> <stop 停止> [uid] [subject]?`      | 取消订阅一个微博账号 |
| `/<wuser 微博用户> <detail 详情> [subject]?`          | 查看订阅详情     |

`uid` 是用户的ID，可以在用户的主页获得，  
例如 <https://www.weibo.com/u/1111681197> 的 `1111681197`  
使用订阅指令后，如果成功找到指定用户，则会回复  
`对@用户名#ID 的监听任务, 添加完成`

### 分组订阅指令

| 指令                                             | 描述          |
|:-----------------------------------------------|:------------|
| `/<wgroup 微博分组> <list 列表>`                     | 列出当前账号的微博分组 |
| `/<wgroup 微博分组> <add task 订阅> [id] [subject]?` | 订阅一个微博分组    |
| `/<wgroup 微博分组> <stop 停止> [id] [subject]?`     | 取消订阅一个微博分组  |
| `/<wgroup 微博分组> <detail 详情> [subject]?`        | 查看订阅详情      |

`id` 是分组的GID或者TITLE，GID可以在分组的页面获得，   
例如 <https://www.weibo.com/mygroups?gid=3893924734832698> 的 `3893924734832698`  
也可以通过列表指令获得，使用列表指令之后会按行回复 `title -> gid`  
使用订阅指令后，如果成功找到指定分组，则会回复  
`对分组标题#ID的监听任务, 添加完成`

### 热搜订阅指令

| 指令                                             | 描述         |
|:-----------------------------------------------|:-----------|
| `/<whot 微博热搜> <add task 订阅> [word] [subject]?` | 订阅一个微博热搜   |
| `/<whot 微博热搜> <stop 停止> [word] [subject]?`     | 取消订阅一个微博热搜 |
| `/<whot 微博热搜> <detail 详情> [subject]?`          | 查看订阅详情     |

### 博文查看指令

| 指令                           | 描述       |
|:-----------------------------|:---------|
| `/<wdetail blog 微博详情> [mid]` | 查看指定微博内容 |

### 关注指令

| 指令                      | 描述     |
|:------------------------|:-------|
| `/<wfollow 微博关注> [uid]` | 关注指定用户 |

## 解析微博链接

机器人会将群里中的微博链接捕获，并将微博内容回复给发送微博链接的人   
这个功能默认开启，通过权限 `xyz.cssxsh.mirai.plugin.weibo-helper:quiet.group` 设置不开启的群聊

## 配置

位于 `Mirai-Console` 运行目录下的 `config/xyz.cssxsh.mirai.plugin.weibo-helper` 文件夹下的 `WeiboHelperSettings` 文件

* `cache` 图片缓存位置
* `expire` 图片缓存过期时间，单位小时，默认3天，为0时不会过期
* `following` 是否清理收藏的用户的缓存，默认 true
* `fast` 快速轮询间隔，单位分钟
* `slow` 慢速轮询间隔，单位分钟
* `contact` 登录状态失效联系人，当微博的登录状态失效时会向这个QQ号发送消息
* `repost` 微博订阅器，最少转发数过滤器，只对列表订阅生效，默认16
* `users` 微博订阅器，屏蔽用户
* `regexes` 微博订阅器，屏蔽的关键词正则表达式
* `urls` 微博订阅器，屏蔽的URL类型, 屏蔽视频可以尝试填入`39`
* `video` 发送微博视频文件
* `emoticon` 处理微博表情
* `picture` 图片设置
* `cover` 封面设置
* `history` 历史记录保留时间，单位天，默认 7d
* `timeout` Http 超时时间，单位毫秒，默认 60_000 ms
* `forward` 以转发消息的方式发送订阅微博
* `show_url` 是否显示url

## 图片设置

有四种设置方案

* `{ type: none, value: {} }` 一张也不显示
* `{ type: all, value: {} }` 全部显示
* `{ type: limit, value: { total: 3 } }` 最多显示total张
* `{ type: top, value: { total: 3 } }` 超过total张一张也不显示

### quiet.group

安静群聊, 不解析URL链接, 通过权限系统配置  
`/perm add g12345 xyz.cssxsh.mirai.plugin.weibo-helper:quiet.group`

## 安装

### MCL 指令安装

`./mcl --update-package xyz.cssxsh:weibo-helper --channel stable --type plugin`

### 手动安装

* 运行 [Mirai Console](https://github.com/mamoe/mirai-console) 生成 `plugins` 文件夹
* 从 [Releases](https://github.com/cssxsh/weibo-helper/releases) 下载 `jar` 并将其放入 `plugins` 文件夹中
