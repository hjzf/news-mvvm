package com.hjzf.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.hjzf.databinding.FragmentUserBinding

class UserFragment : Fragment() {

    private val viewModel: UserViewModel by viewModels()

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.refresh()
        binding.testBtn.setOnClickListener { viewModel.refresh() }
        viewModel.logs.observe(viewLifecycleOwner) {
            val table = binding.apiTable
            // 刷新到界面上
            table.tableData?.clear() // 清空之前的数据
            table.config.minTableWidth = getScreenWidth() // 表格宽度为撑满屏幕
            table.setData(it) // 将新数据放进去
        }
    }

    private fun getScreenWidth(): Int {
        val metrics = resources.displayMetrics
        return metrics.widthPixels
    }

}