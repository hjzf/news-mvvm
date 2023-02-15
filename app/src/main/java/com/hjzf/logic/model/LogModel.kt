package com.hjzf.logic.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bin.david.form.annotation.SmartColumn
import com.bin.david.form.annotation.SmartTable

@Entity(tableName = "network_log")
data class NetworkLog(
    val juHeKey: String,
    val category: String,
    val timestamp: String, // 用字符串存储时间
    // id 默认自增
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
)

@SmartTable(name = "今天的网络请求统计表")
data class LogVO(
    @SmartColumn(id = 1, name = "新闻类别")
    var category: String = "",
    @SmartColumn(id = 2, name = "参数")
    var type: String = "",
    @SmartColumn(id = 3, name = "今日请求次数")
    var count: Int = 0
)