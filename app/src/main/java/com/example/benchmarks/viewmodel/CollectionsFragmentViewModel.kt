package com.example.benchmarks.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.benchmarks.ExecutingListener
import com.example.benchmarks.ListOperationsListener
import com.example.benchmarks.model.*
import kotlinx.coroutines.launch

class CollectionsFragmentViewModel(
    private val listOperationsService: ListOperationsService
) : ViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    private val operationsListener: ListOperationsListener = {
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
            listOperationsService.setCollectionsSize(size ?: 0)
            false
        } else {
            listOperationsService.setCollectionsSize(size)
            _state.value = CollectionsSize(size)
            true
        }
    }

    fun startExecution(enteredText: String) {
        if (validateCollectionSize(enteredText)) {
            viewModelScope.launch {
                listOperationsService.startOperations()
            }
        }
    }

    fun stopExecution() {
        viewModelScope.launch {
            listOperationsService.cancelCoroutine()
        }
    }

    private fun loadOperations() {
        listOperationsService.addListListeners(operationsListener)
        listOperationsService.addExecutingListeners(executingListener)
    }

    override fun onCleared() {
        super.onCleared()
        listOperationsService.cancelCoroutine()
    }
}