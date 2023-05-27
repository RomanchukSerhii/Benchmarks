package com.example.benchmarks.model

import com.example.benchmarks.model.enums.CollectionsName
import com.example.benchmarks.model.enums.CollectionOperations
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.ArrayList

typealias ListOperationsListener = (operations: List<Operation>) -> Unit
typealias ExecutingListener = (isExecuting: Boolean) -> Unit

class ListOperationsService {

    private var operations = mutableListOf<Operation>()
    private val listOperationsListeners = mutableSetOf<ListOperationsListener>()
    private val listExecutingListeners = mutableSetOf<ExecutingListener>()
    private var copyOnWriteArrayList = CopyOnWriteArrayList<Int>()
    private val myCoroutineScope = CoroutineScope(Dispatchers.Default)
    private var mainJob: Job? = null

    init {
        for (collection in CollectionsName.values()) {
            for (operation in CollectionOperations.values()) {
                operations.add(
                    Operation(
                        operation.operationName,
                        collection.collectionName,
                        "0",
                        false
                    )
                )
            }
        }
    }

    suspend fun startOperations(size: Int) {
        startExecute()
        mainJob = myCoroutineScope.launch {
//            if (isActive) {
                val arrayListType = CollectionsName.ARRAY_LIST
                val linkedListType = CollectionsName.LINKED_LIST
                val fillListJob = myCoroutineScope.launch { fillCopyOnWriteArrayList(size) }

                launch { addingInTheBeginningExecutionTime(arrayListType, size) }
                launch { addingInTheMiddleExecutionTime(arrayListType, size) }
                launch { addingInTheEndExecutionTime(arrayListType, size) }
                launch { removingInTheBeginningExecutionTime(arrayListType, size) }
                launch { removingInTheMiddleExecutionTime(arrayListType, size) }
                launch { removingInTheEndExecutionTime(arrayListType, size) }
                launch { searchByValueExecutionTime(arrayListType, size) }

                launch { addingInTheBeginningExecutionTime(linkedListType, size) }
                launch { addingInTheMiddleExecutionTime(linkedListType, size) }
                launch { addingInTheEndExecutionTime(linkedListType, size) }
                launch { removingInTheBeginningExecutionTime(linkedListType, size) }
                launch { removingInTheMiddleExecutionTime(linkedListType, size) }
                launch { removingInTheEndExecutionTime(linkedListType, size) }
                launch { searchByValueExecutionTime(linkedListType, size) }

                fillListJob.join()
                val collectionType = CollectionsName.COPY_ON_WRITE_ARRAY_LIST
                launch { addingInTheBeginningExecutionTime(collectionType, size) }
                launch { addingInTheMiddleExecutionTime(collectionType, size) }
                launch { addingInTheEndExecutionTime(collectionType, size) }
                launch { removingInTheBeginningExecutionTime(collectionType, size) }
                launch { removingInTheMiddleExecutionTime(collectionType, size) }
                launch { removingInTheEndExecutionTime(collectionType, size) }
                launch { searchByValueExecutionTime(collectionType, size) }
            }
//        }

        mainJob?.invokeOnCompletion {
            notifyExecutingChanges(false)
        }
    }

    private fun startExecute() {
        updateList()
        for (index in 0 until operations.size) {
            val updateOperation = operations[index].copy(isExecuted = true)
            operations[index] = updateOperation
        }
        notifyListChanges()
        notifyExecutingChanges(true)
    }

    private fun stopExecute() {
        updateList()
        for (index in 0 until operations.size) {
            val updateOperation = operations[index].copy(isExecuted = false)
            operations[index] = updateOperation
        }
        notifyListChanges()
//        notifyExecutingChanges(true)
    }

    private fun fillCopyOnWriteArrayList(size: Int) {
        val list = fillArrayList(size)
        copyOnWriteArrayList = CopyOnWriteArrayList(list)
    }

    private fun fillArrayList(size: Int): ArrayList<Int> {
        val list = ArrayList<Int>()
        for (i in 0 until size) {
            if( i == size / 2) {
                list.add(REQUIRED_VALUE)
                continue
            }
            list.add(DIGIT_TO_FILL)
        }
        return list
    }

    private fun fillLinkedList(size: Int): LinkedList<Int> {
        val list = LinkedList<Int>()
        for (i in 0 until size) {
            if( i == size / 2) {
                list.add(REQUIRED_VALUE)
                continue
            }
            list.add(DIGIT_TO_FILL)
        }
        return list
    }

    private suspend fun addingInTheBeginningExecutionTime(collectionType: CollectionsName, size: Int) {
        val operationName = CollectionOperations.ADDING_IN_THE_BEGINNING
        val executionTime = getExecutionTime(collectionType, operationName, size)
        setResult( collectionType, operationName, executionTime )
    }

    private suspend fun addingInTheMiddleExecutionTime(collectionType: CollectionsName, size: Int) {
        val operationName = CollectionOperations.ADDING_IN_THE_MIDDLE
        val executionTime = getExecutionTime(collectionType, operationName, size)
        setResult(collectionType, operationName, executionTime)
    }

    private suspend fun addingInTheEndExecutionTime(collectionType: CollectionsName, size: Int) {
        val operationName = CollectionOperations.ADDING_IN_THE_END
        val executionTime = getExecutionTime(collectionType, operationName, size)
        setResult(collectionType, operationName, executionTime)
    }

    private suspend fun removingInTheBeginningExecutionTime(collectionType: CollectionsName, size: Int) {
        val operationName = CollectionOperations.REMOVING_IN_THE_BEGINNING
        val executionTime = getExecutionTime(collectionType, operationName, size)
        setResult(collectionType, operationName, executionTime)
    }

    private suspend fun removingInTheMiddleExecutionTime(collectionType: CollectionsName, size: Int) {
        val operationName = CollectionOperations.REMOVING_IN_THE_MIDDLE
        val executionTime = getExecutionTime(collectionType, operationName, size)
        setResult(collectionType, operationName, executionTime)
    }

    private suspend fun removingInTheEndExecutionTime(collectionType: CollectionsName, size: Int) {
        val operationName = CollectionOperations.REMOVING_IN_THE_END
        val executionTime = getExecutionTime(collectionType, operationName, size)
        setResult(collectionType, operationName, executionTime)
    }

    private suspend fun searchByValueExecutionTime(collectionType: CollectionsName, size: Int) {
        val operationName = CollectionOperations.SEARCH_BY_VALUE
        val executionTime = getExecutionTime(collectionType, operationName, size)
        setResult(collectionType, operationName, executionTime)
    }

    private fun getExecutionTime(
        collectionType: CollectionsName,
        operationName: CollectionOperations,
        size: Int
    ): String {
        if (mainJob?.isActive == true) {
            val collection = when (collectionType) {
                CollectionsName.ARRAY_LIST -> fillArrayList(size)
                CollectionsName.LINKED_LIST -> fillLinkedList(size)
                CollectionsName.COPY_ON_WRITE_ARRAY_LIST -> {
                    copyOnWriteArrayList
                }
            }

            val middleIndex = collection.size / 2
            val startTime = System.currentTimeMillis()
            with(collection) {
                when (operationName) {
                    CollectionOperations.ADDING_IN_THE_BEGINNING -> add(
                        BEGINNING_INDEX,
                        DIGIT_TO_ADD
                    )
                    CollectionOperations.ADDING_IN_THE_MIDDLE -> add(middleIndex, DIGIT_TO_ADD)
                    CollectionOperations.ADDING_IN_THE_END -> add(DIGIT_TO_ADD)
                    CollectionOperations.REMOVING_IN_THE_BEGINNING -> removeAt(BEGINNING_INDEX)
                    CollectionOperations.REMOVING_IN_THE_MIDDLE -> removeAt(middleIndex)
                    CollectionOperations.REMOVING_IN_THE_END -> removeAt(collection.lastIndex)
                    CollectionOperations.SEARCH_BY_VALUE -> find { it == REQUIRED_VALUE }
                }
            }
            val finishTime = System.currentTimeMillis()
            return (finishTime - startTime).toString()
        } else {
            throw CancellationException()
        }
    }

    private suspend fun setResult(
        collectionsType: CollectionsName,
        operationName: CollectionOperations,
        executionTime: String
    ) {
        withContext(Dispatchers.Main) {
            updateList()
            val collectionName = collectionsType.collectionName
            val operation = operationName.operationName
            val index = operations.indexOfFirst {
                it.operationName == operation && it.collectionName == collectionName
            }
            if (index == UNDEFINED) return@withContext
            val updateOperation = operations[index].copy(
                averageExecutionTime = executionTime,
                isExecuted = false
            )
            operations[index] = updateOperation
            notifyListChanges()
        }
    }

    fun cancelCoroutine() {
        stopExecute()
        mainJob?.cancel()
    }
    fun addListListeners(listener: ListOperationsListener) {
        listOperationsListeners.add(listener)
        listener.invoke(operations)
    }

    fun addExecutingListeners(listener: ExecutingListener) {
        listExecutingListeners.add(listener)
        listener.invoke(false)
    }

    fun removeListListeners(listener: ListOperationsListener) {
        listOperationsListeners.remove(listener)
    }

    fun removeExecutingListeners(listener: ExecutingListener) {
        listExecutingListeners.remove(listener)
    }

    private fun updateList() {
        operations = ArrayList(operations)
    }

    private fun notifyExecutingChanges(isExecuting: Boolean) {
        listExecutingListeners.forEach { it.invoke(isExecuting) }
    }

    private fun notifyListChanges() {
        listOperationsListeners.forEach { it.invoke(operations) }
    }

    companion object {
        private const val TAG = "ListOperationsService"
        private const val DIGIT_TO_FILL = 0
        private const val DIGIT_TO_ADD = 1
        private const val BEGINNING_INDEX = 0
        private const val UNDEFINED = -1
        private const val REQUIRED_VALUE = 7
    }
}