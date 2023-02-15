package com.hjzf.logic

import com.hjzf.NewsApplication
import com.hjzf.logic.dao.AppDatabase
import com.hjzf.logic.model.NetworkLog
import com.hjzf.logic.model.News
import com.hjzf.logic.model.Result
import com.hjzf.logic.network.NewsNetwork
import com.hjzf.util.LogUtil
import com.hjzf.util.getCurrentTimeStr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

// Repository层  (仓库层的数据来自"本地数据源"和"网络数据源")
object Repository {
    suspend fun searchNewsFromNetwork(type: String, from: Long, count: Long): Result<List<News>> {
        // 耗时的逻辑要指定为子线程 Dispatchers.IO
        return withContext(Dispatchers.IO) {
            // 调用网络数据源
            try {
                val category = NewsApplication.newsTypeChineseName[type] ?: error("")
                LogUtil.e(TAG, "发送网络请求：$category $from,$count")
                val newsResponse = NewsNetwork.searchNews(type) // 耗时操作
                when (newsResponse.error_code) {
                    0 -> {
                        // 错误码为 0 代表成功
                        var newsList = newsResponse.result.data
                        val database = AppDatabase.getInstance()
                        // 写入日志
                        val log = NetworkLog(NewsApplication.KEY, category, getCurrentTimeStr())
                        database.logDao().insertAll(log)
                        // // 将数据库作为唯一信任源,显示在界面中的数据只能从数据库中拿
                        val newsDao = database.newsDao()
                        newsDao.insertAll(*newsList.toTypedArray()) // 将数据插入数据库作为缓存
                        newsList = newsDao.getNewsByCategory(category, from, count)
                        Result.success(newsList)
                    }
                    10012, 10013 -> Result.failure("每天最多只能免费使用100次聚合数据的api")
                    else -> Result.failure("聚合数据的api发生了错误")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e.message ?: "网络数据获取失败")
            }
        }
    }

    suspend fun searchNewsFromDatabase(type: String, from: Long, count: Long): Result<List<News>> {
        // 耗时的逻辑要指定为子线程 Dispatchers.IO
        return withContext(Dispatchers.IO) {
            // 调用本地数据源
            try {
                val category = NewsApplication.newsTypeChineseName[type] ?: error("")
                LogUtil.e(TAG, "访问数据库：$category $from,$count")
                val newsDao = AppDatabase.getInstance().newsDao()
                val newsList = newsDao.getNewsByCategory(category, from, count)
                Result.success(newsList)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e.message ?: "获取缓存数据失败")
            }
        }
    }

    suspend fun searchTodayLogsFromDatabase(type: String): Result<List<NetworkLog>> {
        // 耗时的逻辑要指定为子线程 Dispatchers.IO
        return withContext(Dispatchers.IO) {
            try {
                val category = NewsApplication.newsTypeChineseName[type] ?: error("")
                LogUtil.e(TAG, "查询log：$category")
                val logDao = AppDatabase.getInstance().logDao()
                val todayLogs = logDao.getTodayLogsByCategory(category)
                Result.success(todayLogs)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e.message ?: "日志查询失败")
            }
        }
    }

    const val TAG = "Repository"

}