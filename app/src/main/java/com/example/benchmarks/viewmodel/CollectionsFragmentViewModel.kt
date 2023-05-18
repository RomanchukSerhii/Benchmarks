package com.example.benchmarks.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.benchmarks.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.LinkedList
import java.util.concurrent.CopyOnWriteArrayList

class CollectionsFragmentViewModel(
    private val operationsService: OperationsService
) : ViewModel() {


    private val _operations = MutableLiveData<List<Operation>>()
    val operations: LiveData<List<Operation>> = _operations

    private val _collectionsState = MutableLiveData<CollectionsState>()
    val collectionsState: LiveData<CollectionsState> = _collectionsState

    private val _progress = MutableLiveData<Progress>()
    val progress: LiveData<Progress> = _progress

    private val listener: ListOperationListener = {
        _operations.value = it
    }

    init {
        loadOperations()
    }

    fun startCollections() {
        operationsService.startOperations()
//        _progress.value = Progress
//        viewModelScope.launch() {
//            calculateAddingInTheBeginning(arrayList)
//            calculateAddingInTheBeginning(linkedList)
//            calculateAddingInTheBeginning(copyOnWrite)
//        }

    }

    private suspend fun calculateAddingInTheBeginning(collectionType: Collection<Int>) {
        withContext(Dispatchers.Default) {
             val collection = when (collectionType) {
                is ArrayList -> collectionType
                is LinkedList -> collectionType
                is CopyOnWriteArrayList -> collectionType
                else -> throw RuntimeException()
            }

            val startTime = System.currentTimeMillis()
            collection.add(0, 7)
            val finishTime = System.currentTimeMillis()
            val result = (finishTime - startTime).toString()

            withContext(Dispatchers.Main) {
                when (collection) {
                    is ArrayList<*> -> _collectionsState.value = ArrayListAddingInTheBeginning(result)
                    is LinkedList<*> -> _collectionsState.value = LinkedListAddingInTheBeginning(result)
                    is CopyOnWriteArrayList<*> -> _collectionsState.value = CopyOnWriteAddingInTheBeginning(result)
                }
            }

        }
    }

    private fun loadOperations() {
        operationsService.addListListeners(listener)
    }

    companion object {
        private const val DIGIT_TO_FILL = 0
    }
}