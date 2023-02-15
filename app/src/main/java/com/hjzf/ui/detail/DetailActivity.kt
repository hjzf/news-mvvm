package com.hjzf.ui.detail

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.hjzf.R
import com.hjzf.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    companion object {
        private const val ARG_NEWS_URL = "news_URL"
        private const val ARG_DETAIL_ACTIVITY_TITLE = "detail_activity_title"

        @JvmStatic
        fun actionStart(context: Context, newsURL: String, activityTitle: String) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(ARG_NEWS_URL, newsURL)
            intent.putExtra(ARG_DETAIL_ACTIVITY_TITLE, activityTitle)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        // 设置标题栏
        setSupportActionBar(binding.detailToolBar)
        // 去掉默认的标题
        title = ""
        // 设置标题的居中文字
        binding.detailRealTitle.text = intent.getStringExtra(ARG_DETAIL_ACTIVITY_TITLE) ?: ""
        // 设置系统自带的home按钮(小箭头)可见
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 打开新闻网页
        val url = intent.getStringExtra(ARG_NEWS_URL)
        if (url != null) {
            binding.newsWebView.loadUrl(url)
        }
    }

    // 给Activity加载标题栏的菜单项
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //写一个menu的资源文件.然后创建就行了.
        menuInflater.inflate(R.menu.detail_tool_bar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //给系统自带的home按钮(小箭头)添加点击事件：销毁本页面返回上一级
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}