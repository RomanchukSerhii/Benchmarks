package com.example.benchmarks.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.benchmarks.R
import com.example.benchmarks.databinding.ActivityMainBinding
import com.example.benchmarks.view.adapters.ViewPagerAdapter
import com.example.benchmarks.view.screens.CollectionsFragment
import com.example.benchmarks.view.screens.MapsFragment
import com.example.benchmarks.view.screens.ValidateDialogFragment
import com.example.benchmarks.view.screens.ValidateDialogListener
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.properties.Delegates.notNull

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val collectionsFragment by lazy {
        CollectionsFragment()
    }

    private val mapsFragment by lazy {
        MapsFragment()
    }

    private var collectionsSize by notNull<Int>()
    private var isExecuted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (savedInstanceState != null) {
            collectionsSize = savedInstanceState.getInt(ARG_COLLECTIONS_SIZE)
            isExecuted = savedInstanceState.getBoolean(KEY_EXECUTED_STATE)
            updateUi()
        }
        if (isExecuted) {
            setViewPagerAdapter()
            showViewPager()
        } else {
            setViewPagerAdapter()
            setClickListeners()
            setupValidateDialogFragmentListener()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ARG_COLLECTIONS_SIZE, collectionsSize)
        outState.putBoolean(KEY_EXECUTED_STATE, isExecuted)
    }

    private fun setViewPagerAdapter() {
        val fragmentsList = fillFragmentsList()
        val viewPagerAdapter =
            ViewPagerAdapter(supportFragmentManager, lifecycle, fragmentsList)
        binding.viewPager.adapter = viewPagerAdapter
        setUpTabLayout()
    }

    private fun setUpTabLayout() {
        with(binding) {

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                when (position) {
                    0 -> {
                        tab.view.setBackgroundResource(R.drawable.tab_background_selector_left)
                        tab.text = getString(R.string.collections_title)
                    }
                    1 -> {
                        tab.view.setBackgroundResource(R.drawable.tab_background_selector_right)
                        tab.text = getString(R.string.maps_title)
                    }
                }
            }.attach()

            for (i in 0..1) {
                val textView = LayoutInflater
                    .from(this@MainActivity)
                    .inflate(R.layout.tab_title, null)
                binding.tabLayout.getTabAt(i)?.customView = textView
            }
        }
    }

    private fun fillFragmentsList(): List<Fragment> {
        return listOf(
            collectionsFragment,
            mapsFragment
        )
    }

    private fun setClickListeners() {
        binding.buttonCalculate.setOnClickListener {
            val enteredText = binding.etEnterCollectionSize.text.toString()
            if (enteredText.isBlank()) {
                binding.etEnterCollectionSize.error = "Error. You need enter elements count."
                return@setOnClickListener
            }
            collectionsSize = enteredText.toInt()
            if (collectionsSize < 1_000_000 || collectionsSize > 10_000_000) {
                showValidateDialogFragment(collectionsSize)
            } else {
                collectionsFragment.arguments = bundleOf(ARG_COLLECTIONS_SIZE to collectionsSize)
                mapsFragment.arguments = bundleOf(ARG_COLLECTIONS_SIZE to collectionsSize)
                showViewPager()
                isExecuted = true
            }
        }
    }

    private fun showViewPager() {
        with(binding) {
            tvEnterCollectionTitle.visibility = View.GONE
            tilEnterCollectionSize.visibility = View.GONE
            viewSpace.visibility = View.GONE
            buttonCalculate.visibility = View.GONE
            viewPager.visibility = View.VISIBLE
        }
    }

    private fun showValidateDialogFragment(inputNumber: Int) {
        ValidateDialogFragment.show(supportFragmentManager, inputNumber, MAIN_ACTIVITY_REQUEST_KEY)
    }

    private fun setupValidateDialogFragmentListener() {
        val listener: ValidateDialogListener = { requestKey, inputNumber ->
            if (requestKey == MAIN_ACTIVITY_REQUEST_KEY) {
                collectionsSize = inputNumber
            }
            updateUi()
        }
        ValidateDialogFragment.setupListener(
            supportFragmentManager,
            this,
            MAIN_ACTIVITY_REQUEST_KEY,
            listener
        )
    }

    private fun updateUi() {
        binding.etEnterCollectionSize.setText(collectionsSize.toString())
    }

    companion object {
        private const val ARG_COLLECTIONS_SIZE = "ARG_COLLECTIONS_SIZE"
        private const val KEY_EXECUTED_STATE = "KEY_EXECUTED_STATE"
        private const val MAIN_ACTIVITY_REQUEST_KEY = "MAIN_ACTIVITY_REQUEST_KEY"
    }
}