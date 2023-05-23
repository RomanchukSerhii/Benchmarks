package com.example.benchmarks.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.benchmarks.model.*
import kotlinx.coroutines.launch

class CollectionsFragmentViewModel(
    private val listOperationsService: ListOperationsService
) : ViewModel() {

    private val _operations = MutableLiveData<List<Operation>>()
    val operations: LiveData<List<Operation>> = _operations

    private val listener: ListOperationsListener = {
        _operations.value = it
    }

    init {
        loadOperations()
    }

    fun startCollections(size: Int) {
        viewModelScope.launch {
            listOperationsService.startOperations(size)
        }
    }

    private fun loadOperations() {
        listOperationsService.addListListeners(listener)
    }

    override fun onCleared() {
        super.onCleared()
        listOperationsService.cancelCoroutine()
    }
}