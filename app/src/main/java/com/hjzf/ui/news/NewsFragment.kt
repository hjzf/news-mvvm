package com.hjzf.ui.news

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hjzf.databinding.FragmentNewsBinding
import com.hjzf.util.showToast

class NewsFragment : Fragment() {

    companion object {
        const val TAG = "NewsFragment"
        private const val ARG_NEWS_TYPE = "news_type"

        /**
         * 通过静态方法 newInstance获得一个 NewsFragment对象，
         * 并将参数 newsType 传递到 NewsFragment内部的 viewModel中
         */
        @JvmStatic
        fun newInstance(newsType: String): NewsFragment {
            return NewsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_NEWS_TYPE, newsType)
                }
            }
        }
    }

    val viewModel: NewsViewModel by viewModels()

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.apply {
            // 设置当前页面对应的新闻类别
            setNewsType(arguments?.getString(ARG_NEWS_TYPE) ?: "")
        }
        val rv = binding.newsRecyclerView
        rv.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        val newsAdapter = NewsAdapter(viewModel)
        rv.adapter = newsAdapter
        // 功能1:滑动到最底部时从数据库加载更多
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                if (dy < 0) return // 不监听上滑的动作
                val linearLayoutManager = rv.layoutManager as LinearLayoutManager
                val lastPosition = linearLayoutManager.findLastVisibleItemPosition()
                if (lastPosition == newsAdapter.itemCount - 1) {
                    //当footer_view可见时 <==> 滑动到最底部时
                    viewModel.loadCacheData()
                }
            }
        })
        // 功能2:下拉刷新,用网络数据替换掉当前页面中的所有数据
        val srl = binding.newsSwipeRefreshLayout
        srl.setColorSchemeColors(Color.parseColor("#0288D1")) // 进度条的颜色
        viewModel.srlIsRefreshing.observe(viewLifecycleOwner) { srl.isRefreshing = it }
        srl.setOnRefreshListener { viewModel.loadNetWorkData() }

        // 通过LiveData观察数据变化来刷新UI
        viewModel.status.observe(viewLifecycleOwner) {
            newsAdapter.footerViewStatus = it
            // notify触发onBindViewHolder的执行,刷新footer_view的UI
            newsAdapter.notifyItemChanged(newsAdapter.itemCount - 1)
        }
        viewModel.newsList.observe(viewLifecycleOwner) {
            newsAdapter.submitList(it) { // commitCallback
                if (viewModel.shouldScrollToTop) {
                    // 页面数据刷新之后，自动回到顶部
                    rv.scrollToPosition(0)
                }
            }
            viewModel.setSrlRefreshing(false) // 让下拉刷新的圆圈停止转动并隐藏
            viewModel.isLoading = false // 加载完成
        }
        viewModel.message.observe(viewLifecycleOwner) {
            if (!it.isNullOrBlank()) {
                // 防止把空白字符串弹出来影响用户体验
                it.showToast()
                viewModel.setMessage("")
            }
        }
    }

}