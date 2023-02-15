package com.hjzf.logic.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hjzf.NewsApplication
import com.hjzf.logic.model.NetworkLog
import com.hjzf.logic.model.News

@Database(entities = [News::class, NetworkLog::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao

    abstract fun logDao():LogDao

    companion object {
        private const val DB_NAME = "news_database.db"
        private var instance: AppDatabase? = null

        // 单例模式
        fun getInstance(context: Context = NewsApplication.context): AppDatabase {
            if (instance == null) {
                synchronized(AppDatabase::class.java) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        DB_NAME
                    ).build()
                }
            }
            return instance!!
        }
    }
}
