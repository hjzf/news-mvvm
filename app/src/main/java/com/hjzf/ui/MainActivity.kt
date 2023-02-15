package com.hjzf.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.hjzf.R
import com.hjzf.databinding.ActivityMainBinding
import com.hjzf.util.LogUtil

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        LogUtil.e(TAG, "onCreate: ")
        super.onCreate(savedInstanceState)
        // 设置 View binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 在xml中为 nav_host_container 添加两个属性 :
        // android:name="androidx.navigation.fragment.NavHostFragment"
        // app:navGraph="@navigation/nav_graph"

        // (1)初始化 navController,
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.navController

        // (2)关联 “底部导航栏”与“navController”, 导航路由见 app/src/main/res/navigation/nav_graph.xml
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        LogUtil.e(TAG, "onSupportNavigateUp: ")
        return navController.navigateUp()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}