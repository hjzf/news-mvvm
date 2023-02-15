package com.hjzf.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 为了使用 NewsService,必须创建一个Retrofit构建器如下
object ServiceCreator {
    // 拼接请求完整的url。
    // 这是一个http请求，会被部分手机阻止
    // 默认只允许https请求，解决方案：https://blog.csdn.net/sinat_34115945/article/details/89888035
    private const val BASE_URL = "http://v.juhe.cn/toutiao/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)
    inline fun <reified T> create(): T = create(T::class.java)
}