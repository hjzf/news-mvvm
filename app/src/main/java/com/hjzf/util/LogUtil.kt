@file:Suppress("unused")

package com.hjzf.util

import android.util.Log

// Singleton class
object LogUtil {
    private const val VERBOSE = 1
    private const val DEBUG = 2
    private const val INFO = 3
    private const val WARN = 4
    private const val ERROR = 5

    // "LEVEL = VERBOSE" means Logs with level 'VERBOSE' or higher will be printed
    private var LEVEL = VERBOSE
    // produce
    //private var LEVEL = 6

    fun v(tag: String, msg: String) {
        if (LEVEL <= VERBOSE) {
            Log.v(tag, msg)
        }
    }

    fun d(tag: String, msg: String) {
        if (LEVEL <= DEBUG) {
            Log.d(tag, msg)
        }
    }

    fun i(tag: String, msg: String) {
        if (LEVEL <= INFO) {
            Log.i(tag, msg)
        }
    }

    fun w(tag: String, msg: String) {
        if (LEVEL <= WARN) {
            Log.w(tag, msg)
        }
    }

    fun e(tag: String, msg: String) {
        if (LEVEL <= ERROR) {
            Log.e(tag, msg)
        }
    }
}

fun String.printLog(tag: String = "") {
    LogUtil.i(tag, this)
}