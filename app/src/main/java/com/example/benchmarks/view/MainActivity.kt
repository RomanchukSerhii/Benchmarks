package com.example.benchmarks.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.benchmarks.R
import com.example.benchmarks.databinding.ActivityMainBinding
import com.example.benchmarks.view.adapters.ViewPagerAdapter
import com.example.benchmarks.view.screens.CollectionsFragment
import com.example.benchmarks.view.screens.MapsFragment
import kotlinx.coroutines.launch

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
        setClickListeners()
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