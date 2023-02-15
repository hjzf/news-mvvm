package com.hjzf.util

import android.content.Context
import android.widget.Toast
import com.hjzf.NewsApplication

fun String.showToast(
    duration: Int = Toast.LENGTH_SHORT,
    context: Context = NewsApplication.context
) {
    try {
        Toast.makeText(context, this, duration).show()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}