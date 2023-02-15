package com.hjzf.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

// 网络数据源
object NewsNetwork {
    // 通用的网络请求方法封装，方便.await()
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                // 成功
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (null != body) {
                        continuation.resume(body)
                    } else {
                        continuation.resumeWithException(RuntimeException("response body is null!!!"))
                    }
                }

                // 失败
                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    private val newsService = ServiceCreator.create(NewsService::class.java)

    // 网络请求1:获取新闻
    suspend fun searchNews(type: String) = newsService.searchNews(type).await()
    // 网络请求2：xx
    // ....
    // 网络请求3：yy
    // ....
}