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

    fun startExecution(size: Int) {
        if (size < 1_000_000 || size > 10_000_000) {
            _state.value = Error
            return
        }
        viewModelScope.launch {
            mapsOperationsService.startOperations(size)
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