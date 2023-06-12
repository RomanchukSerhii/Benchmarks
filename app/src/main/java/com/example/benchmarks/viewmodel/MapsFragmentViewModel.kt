package com.example.benchmarks.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.benchmarks.ExecutingListener
import com.example.benchmarks.MapsOperationsListener
import com.example.benchmarks.model.MapsOperationsService
import com.example.benchmarks.model.Operation
import kotlinx.coroutines.launch

class MapsFragmentViewModel(
    private val mapsOperationsService: MapsOperationsService
): ViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    private val operationsListener: MapsOperationsListener = {
        _state.value = Result(it)
    }

    private val executingListener: ExecutingListener = {
        _state.postValue(Executing(it))
    }

    init {
        loadOperations()
    }

    fun validateCollectionSize(enteredText: String): Boolean {
        val size = enteredText.toIntOrNull()
        return if (size == null || size < 1_000_000 || size > 10_000_000) {
            _state.value = Error(size ?: 0)
            mapsOperationsService.setCollectionsSize(size ?: 0)
            false
        } else {
            mapsOperationsService.setCollectionsSize(size)
            _state.value = CollectionsSize(size)
            true
        }
    }

    fun startExecution(enteredText: String) {
        if (validateCollectionSize(enteredText)) {
            viewModelScope.launch {
                mapsOperationsService.startOperations()
            }
        }
    }

    fun stopExecution() {
        viewModelScope.launch {
            mapsOperationsService.cancelCoroutine()
        }
    }

    private fun loadOperations() {
        mapsOperationsService.addListListeners(operationsListener)
        mapsOperationsService.addExecutingListeners(executingListener)
    }
}