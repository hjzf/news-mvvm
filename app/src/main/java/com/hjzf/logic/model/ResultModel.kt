@file:Suppress("unused")
package com.hjzf.logic.model

// Repository层统一返回Result对象(暂时这么设计,以后会修改为LiveData)
class Result<T> {
    private var value: T? = null
    private var message: String = ""

    companion object {
        @JvmStatic
        fun <T> success(value: T?): Result<T> {
            val result = Result<T>()
            result.value = value
            return result
        }

        @JvmStatic
        fun <T> failure(message: String): Result<T> {
            val result = Result<T>()
            result.message = message
            return result
        }
    }

    fun getOrNull(): T? = value
    fun getMessage(): String = message

    val isSuccess: Boolean get() = value != null
    val isFailure: Boolean get() = value == null
}