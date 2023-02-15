package com.hjzf.logic.network

import com.hjzf.logic.model.NewsResponse
import com.hjzf.NewsApplication
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService {
    // 拼接请求的参数
    @GET("index?key=${NewsApplication.KEY}")
    fun searchNews(@Query("type") type: String): Call<NewsResponse>
}