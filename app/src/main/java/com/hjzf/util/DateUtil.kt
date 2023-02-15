package com.hjzf.util

import java.text.SimpleDateFormat
import java.util.*

fun getCurrentTimeStr(): String {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    simpleDateFormat.timeZone = TimeZone.getTimeZone("Asia/Shanghai")
    return simpleDateFormat.format(Date())
}