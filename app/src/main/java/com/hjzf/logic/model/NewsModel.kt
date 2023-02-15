package com.hjzf.logic.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

data class NewsResponse(
    val reason: String,
    val result: NewsResult,
    val error_code: Int
)

data class NewsResult(
    val stat: String,
    val data: List<News>
)

// 假设所有新闻的标题互不重复,所以能在这里的title上面建立索引
// 真实的业务千万不要这样处理！！！
@Entity(tableName = "news", indices = [Index(value = ["title"], unique = true)])
data class News(
    // id 自增
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    val title: String,
    val date: String,
    val category: String,
    val author_name: String,
    val thumbnail_pic_s: String,
    val thumbnail_pic_s02: String?,
    val thumbnail_pic_s03: String?,
    val url: String
)