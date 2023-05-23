package com.example.benchmarks

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.benchmarks.viewmodel.CollectionsFragmentViewModel

class ViewModelFactory(
    private val app: App
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when(modelClass) {
            CollectionsFragmentViewModel::class.java -> {
                CollectionsFragmentViewModel(app.listOperationsService)
            }
            else -> throw IllegalStateException("Unknown view model class $modelClass")
        }
        return viewModel as T
    }
}

fun Fragment.factory() = ViewModelFactory(requireContext().applicationContext as App)