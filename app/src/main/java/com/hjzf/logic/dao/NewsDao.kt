@file:Suppress("unused")

package com.hjzf.logic.dao

import androidx.room.*
import com.hjzf.logic.model.News

// 本地数据源,来自SQLite数据库
@Dao
interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE) //同标题的新闻会被忽略掉
    suspend fun insertAll(vararg news: News)

    @Query("SELECT * FROM news WHERE category=:category ORDER BY id DESC LIMIT :from,:count")
    suspend fun getNewsByCategory(category: String, from: Long, count: Long): List<News>

    @Query("SELECT * FROM news")
    suspend fun getAll(): List<News>

    @Query("SELECT * FROM news WHERE id IN (:newsIds)")
    suspend fun loadAllByIds(newsIds: IntArray): List<News>

    @Query("SELECT * FROM news WHERE title = :title")
    suspend fun findByTitle(title: String): News

    @Delete
    suspend fun delete(news: News)
}