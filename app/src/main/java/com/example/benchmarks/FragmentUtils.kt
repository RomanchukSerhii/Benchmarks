package com.example.benchmarks

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.benchmarks.model.Operation
import com.example.benchmarks.viewmodel.CollectionsFragmentViewModel
import com.example.benchmarks.viewmodel.MapsFragmentViewModel

class ViewModelFactory(
    private val app: App
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when(modelClass) {
            CollectionsFragmentViewModel::class.java -> {
                CollectionsFragmentViewModel(app.listOperationsService)
            }
            MapsFragmentViewModel::class.java -> {
                MapsFragmentViewModel(app.mapsOperationsService)
            }
            else -> throw IllegalStateException("Unknown view model class $modelClass")
        }
        return viewModel as T
    }
}

fun Fragment.factory() = ViewModelFactory(requireContext().applicationContext as App)
typealias ListOperationsListener = (operations: List<Operation>) -> Unit
typealias MapsOperationsListener = (operations: List<Operation>) -> Unit
typealias ExecutingListener = (isExecuting: Boolean) -> Unit