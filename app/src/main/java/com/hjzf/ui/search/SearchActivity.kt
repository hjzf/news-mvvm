package com.hjzf.ui.search

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import com.hjzf.databinding.ActivitySearchBinding
import com.hjzf.util.showToast

class SearchActivity : AppCompatActivity() {
    companion object {
        @JvmStatic
        fun actionStart(context: Context) {
            val intent = Intent(context, SearchActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // "取消"的点击事件：销毁本活动，返回上一级
        binding.searchCancelButton.setOnClickListener { finish() }
        // 搜索框回车事件
        binding.searchEditText.setOnEditorActionListener { _, keyCode, _ ->
            // 如果点击了回车键，即搜索键，就弹出一个toast
            if (keyCode == EditorInfo.IME_ACTION_SEARCH) {
                "你输入了${binding.searchEditText.text}".showToast()
                true
            } else {
                false
            }
        }
    }
}