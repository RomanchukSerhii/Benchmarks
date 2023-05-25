package com.example.benchmarks.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.benchmarks.model.MapsOperationsListener
import com.example.benchmarks.model.MapsOperationsService
import com.example.benchmarks.model.Operation
import kotlinx.coroutines.launch

class MapsFragmentViewModel(
    private val mapsOperationsService: MapsOperationsService
): ViewModel() {

    private val _operations = MutableLiveData<List<Operation>>()
    val operations: LiveData<List<Operation>> = _operations

    private val listener: MapsOperationsListener = {
        _operations.value = it
    }

    init {
        loadOperations()
    }

    fun startCollections(size: Int) {
        viewModelScope.launch {
            mapsOperationsService.startOperations(size)
        }
    }

    private fun loadOperations() {
        mapsOperationsService.addListListeners(listener)
    }

    override fun onCleared() {
        super.onCleared()
        mapsOperationsService.cancelCoroutine()
    }
}