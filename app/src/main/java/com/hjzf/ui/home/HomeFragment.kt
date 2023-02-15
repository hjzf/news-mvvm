package com.hjzf.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.hjzf.NewsApplication
import com.hjzf.R
import com.hjzf.databinding.FragmentHomeBinding
import com.hjzf.ui.news.NewsFragment
import com.hjzf.ui.search.SearchActivity
import com.hjzf.util.LogUtil
import com.hjzf.util.showToast

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    //                对应的标题：  头条      国内      国际      娱乐     科技     体育      财经
    private val typeList = listOf("top", "guonei", "guoji", "yule", "keji", "tiyu", "caijing")

    // NewsFragment对象数组
    private val newsFragmentList = typeList.map { NewsFragment.newInstance(it) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        LogUtil.e(TAG, "onCreateView home")
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        LogUtil.e(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        // 动态加载菜单
        binding.homeToolBar.inflateMenu(R.menu.home_tool_bar_menu)
        binding.homeToolBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.refresh -> {
                    val position = binding.newsViewPager.currentItem
                    newsFragmentList[position].viewModel.loadNetWorkData()
                }
            }
            false
        }
        // 设置头像点击事件
        binding.avatar.setOnClickListener { "你点击了头像".showToast() }
        // 设置home页面的搜索框不可编辑
        binding.homeEditText.keyListener = null
        // 设置home页面的搜索框的点击事件: 打开SearchActivity这个页面，即搜索页面
        binding.homeEditText.setOnClickListener { SearchActivity.actionStart(NewsApplication.context) }
        // viewPager用于显示各种类别的NewsFragment
        val viewPager = binding.newsViewPager
        // 设置缓存数量！！！！！ 这里设置为缓存所有页面
        viewPager.offscreenPageLimit = typeList.size
        // 将fragmentList里面的fragment放进viewPager里面，从而渲染到视图上.建议用childFragmentManager
        viewPager.adapter = Adapter(childFragmentManager)
        // 实现viewPager左右滑动与tabLayout这个标签选择器的联动
        val tabLayout = binding.newsTabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = NewsApplication.newsTypeChineseName[typeList[position]] ?: "未知类型"
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class Adapter(fm: FragmentManager) :
        FragmentStateAdapter(fm, this@HomeFragment.lifecycle) {

        override fun getItemCount(): Int = newsFragmentList.size

        override fun createFragment(position: Int): Fragment = newsFragmentList[position]
    }

    companion object {
        const val TAG = "HomeFragment"
    }
}