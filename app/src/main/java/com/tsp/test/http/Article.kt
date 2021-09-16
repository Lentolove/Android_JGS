package com.tsp.test.http

import androidx.annotation.Keep
import java.io.Serializable

/**
 *     author : shengping.tian
 *     time   : 2020/12/12
 *     desc   :
 *     version: 1.0
 */
@Keep
data class Article(
    var apkLink: String? = "",
    var audit: Int = 0,
    var author: String? = "",
    var chapterId: Int = 0,
    var chapterName: String? = "",
    var courseId: Int = 0,
    var desc: String? = "",
    var envelopePic: String? = "",
    var fresh: Boolean = false,
    var id: Long = 0,
    var link: String? = "",
    var niceDate: String? = "",
    var niceShareDate: String? = "",
    var origin: String? = "",
    var originId: Long = 0,
    var prefix: String? = "",
    var projectLink: String? = "",
    var publishTime: Long = 0,
    var selfVisible: Int = 0,
    var shareDate: Long? = 0,
    var shareUser: String? = "",
    var superChapterId: Int = 0,
    var superChapterName: String? = "",
    var tags: MutableList<Tag> = mutableListOf(),
    var title: String? = "",
    var type: Int = 0,
    var userId: Int = 0,
    var visible: Int = 0,
    var zan: Int = 0,
    var top: Boolean = false,
    // 历史记录sqlite数据库专用字段，阅读时间
    var readTime: Long = 0L
):Serializable

data class Tag(
    var articleId: Long = 0,
    var name: String = "",
    var url: String = ""
):Serializable