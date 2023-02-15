package com.hjzf.ui.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hjzf.logic.Repository
import com.hjzf.logic.model.News
import com.hjzf.ui.news.NewsFragment.Companion.TAG
import com.hjzf.util.LogUtil
import com.hjzf.util.isNetworkAvailable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {

    // 这个fragment展示的新闻类别：例如 "yule" "guoji" ...
    private var type: String = ""

    // srl即SwipeRefreshLayout下拉刷新控件的状态, true表示圆圈在转动, false表示停止转动
    private val _srlIsRefreshing = MutableLiveData<Boolean>()
    val srlIsRefreshing: LiveData<Boolean> = _srlIsRefreshing

    // newsList：新闻列表中的数据
    private val _newsList = MutableLiveData<List<News>>().apply { value = ArrayList() }
    val newsList: LiveData<List<News>> = _newsList

    // status：footer_view的状态
    private val _status = MutableLiveData<Int>()
    val status: LiveData<Int> = _status

    // message：弹出的Toast的内容
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    // 是否需要滑动到最顶部
    var shouldScrollToTop = true

    // 起到互斥锁的作用,防抖节流
    var isLoading = false

    fun setSrlRefreshing(value: Boolean) {
        this._srlIsRefreshing.value = value
    }

    fun setNewsType(type: String) {
        this.type = type
    }

    fun setMessage(message: String) {
        this._message.value = message
    }

    fun loadNetWorkData() {
        LogUtil.e(TAG, "loadNetWorkData")
        if (isLoading) return
        isLoading = true
        _srlIsRefreshing.value = true  // 让下拉刷新的圆圈转起来
        viewModelScope.launch {
            delay(700)                   // 这个延迟只是为了视觉效果，没有逻辑作用
            foo1()                                 // 具体的操作交给一个挂起函数执行
        }
    }

    fun loadCacheData() {
        LogUtil.e(TAG, "loadCacheData")
        if (isLoading) return
        isLoading = true
        viewModelScope.launch {
            delay(1000)                     // 这个延迟只是为了视觉效果，没有逻辑作用
            foo2()                                    // 具体的操作交给另一个挂起函数执行
        }
    }

    private suspend fun foo1() {
        if (!isNetworkAvailable()) {
            _status.postValue(ERROR_STATUS)
            _newsList.postValue(_newsList.value)
            _message.postValue("网络连接中断")
            return
        }
        val result = Repository.searchNewsFromNetwork(type, 0, 5)
        if (result.isSuccess) {
            // 成功
            val newData = result.getOrNull()!!
            if (newData.isNotEmpty()) {
                _status.postValue(LOADING_STATUS)
                // 只要发起了网络请求，就自动滑动到最顶部
                shouldScrollToTop = true
                // 更新页面的数据
                _newsList.postValue(newData)
            } else {
                _status.postValue(ERROR_STATUS)
                _newsList.postValue(_newsList.value)
                _message.postValue("无法从网络获取到新数据")
            }
        } else {
            // 失败
            _status.postValue(ERROR_STATUS)
            _newsList.postValue(_newsList.value)
            _message.postValue(result.getMessage())
        }
    }

    private suspend fun foo2() {
        val oldData: List<News> = _newsList.value ?: ArrayList()
        val pageSize = oldData.size.toLong()
        val result = Repository.searchNewsFromDatabase(type, pageSize, 6)
        if (result.isSuccess) {
            // 成功
            val newData = result.getOrNull()!!
            if (newData.isNotEmpty()) {
                _status.postValue(LOADING_STATUS)
                val list = listOf(oldData, newData).flatten()
                // 如果列表元素太少,就自动滑动到最顶部
                shouldScrollToTop = (list.size <= 10)
                // 更新页面的数据
                _newsList.postValue(list)
            } else {
                if (oldData.isNotEmpty()) {
                    _status.postValue(FINISHED_STATUS)
                    _newsList.postValue(_newsList.value)
                } else {// 界面上没数据 缓存中没数据 只能从网络获取
                    _status.postValue(FINISHED_STATUS)
                    _newsList.postValue(_newsList.value)
                    foo1()
                }
            }
        } else {
            // 失败
            _status.postValue(ERROR_STATUS)
            _newsList.postValue(_newsList.value)
            _message.postValue(result.getMessage())
        }
    }

    companion object {
        // 在下滑到 RecyclerView 的最底部后,有下面几种可能的状态
        /**
         * LOADING_STATUS状态：footer_view 的进度条转圈,显示"正在加载中..."
         */
        const val LOADING_STATUS = 996

        /**
         * FINISHED_STATUS状态：footer_view 隐藏进度条,显示"已经没有更多内容了"
         */
        const val FINISHED_STATUS = 997

        /**
         * ERROR_STATUS状态：footer_view 隐藏进度条,显示"加载失败,点击重新加载"
         */
        const val ERROR_STATUS = 998
    }
}