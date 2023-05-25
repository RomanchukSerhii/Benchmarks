package com.example.benchmarks.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import com.example.benchmarks.R
import com.example.benchmarks.databinding.ActivityMainBinding
import com.example.benchmarks.view.adapters.ViewPagerAdapter
import com.example.benchmarks.view.screens.CollectionsFragment
import com.example.benchmarks.view.screens.MapsFragment
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val fragmentsList = fillFragmentsList()
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, lifecycle, fragmentsList)
        binding.viewPager.adapter = viewPagerAdapter
        setUpTabLayout()

        setClickListeners()
    }

    private fun setUpTabLayout() {
        with(binding) {

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                when (position) {
                    0 -> {
                        tab.view.setBackgroundResource(R.drawable.tab_background_selector_left)
                        tab.text = "Collections"
                    }
                    1 -> {
                        tab.view.setBackgroundResource(R.drawable.tab_background_selector_right)
                        tab.text = "Maps"
                    }
                }
            }.attach()

            for (i in 0..1) {
                val textView = LayoutInflater
                    .from(this@MainActivity)
                    .inflate(R.layout.tab_title, null)
                binding.tabLayout.getTabAt(i)?.customView = textView
            }

//            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//                override fun onTabSelected(tab: TabLayout.Tab?) {
//                    tab?.view?.setBackgroundResource(R.drawable.tab_background_selector)
//                }
//
//                override fun onTabUnselected(tab: TabLayout.Tab?) {
//                }
//
//                override fun onTabReselected(tab: TabLayout.Tab?) {
//                }
//
//            })
        }
    }

    private fun fillFragmentsList(): List<Fragment> {
        return listOf(
            CollectionsFragment(),
            MapsFragment()
        )
    }

    private fun setClickListeners() {
        binding.buttonCalculate.setOnClickListener {
            with(binding) {
                tvEnterCollectionTitle.visibility = View.GONE
                tilEnterCollectionSize.visibility = View.GONE
                viewSpace.visibility = View.GONE
                buttonCalculate.visibility = View.GONE
                viewPager.visibility = View.VISIBLE
            }
        }
    }
}