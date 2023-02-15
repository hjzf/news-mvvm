package com.hjzf.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hjzf.NewsApplication
import com.hjzf.logic.Repository
import com.hjzf.logic.model.LogVO
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val _logs = MutableLiveData<List<LogVO>>()
    val logs: LiveData<List<LogVO>> = _logs

    fun refresh() {
        viewModelScope.launch {
            val list = ArrayList<LogVO>()
            var sum = 0
            for ((type, category) in NewsApplication.newsTypeChineseName) {
                val result = Repository.searchTodayLogsFromDatabase(type)
                if (result.isSuccess) {
                    val count = result.getOrNull()!!.size
                    sum += count
                    val logVO = LogVO(category, type, count)
                    list.add(logVO)
                }
            }
            val logVO = LogVO("合计", "", sum)
            list.add(logVO)
            _logs.value = list
        }
    }
}