package com.example.benchmarks.model

import com.example.benchmarks.model.enums.CollectionsName
import com.example.benchmarks.model.enums.OperationsName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.ArrayList

typealias OperationsListener = (operations: List<Operation>) -> Unit

class ListOperationsService {

    private var operations = mutableListOf<Operation>()
    private val listeners = mutableSetOf<OperationsListener>()
    private val arrayList = arrayListOf<Int>()
    private val linkedList = LinkedList<Int>()
    private val copyOnWriteArrayList = CopyOnWriteArrayList<Int>()

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        for (collection in CollectionsName.values()) {
            for (operation in OperationsName.values()) {
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

    fun startOperations(size: Int) {
        startExecute()
        fillLists(size)
        coroutineScope.launch {
            addingInTheBeginningExecutionTime(arrayList)
            addingInTheBeginningExecutionTime(linkedList)
            addingInTheBeginningExecutionTime(copyOnWriteArrayList)
            addingInTheMiddleExecutionTime(arrayList)
            addingInTheMiddleExecutionTime(linkedList)
            addingInTheMiddleExecutionTime(copyOnWriteArrayList)
            addingInTheEndExecutionTime(arrayList)
            addingInTheEndExecutionTime(linkedList)
            addingInTheEndExecutionTime(copyOnWriteArrayList)
        }
    }

    private fun startExecute() {
        updateList()
        for (index in 0 until operations.size) {
            val updateOperation = operations[index].copy(isExecuted = true)
            operations[index] = updateOperation
        }
        notifyListChanges()
    }

    private fun fillLists(size: Int) {
        repeat(size) {
            arrayList.add(DIGIT_TO_FILL)
            linkedList.add(DIGIT_TO_FILL)
            copyOnWriteArrayList.add(DIGIT_TO_FILL)
        }
    }

    private suspend fun addingInTheBeginningExecutionTime(collectionType: Collection<Int>) {
        val collection = when (collectionType) {
            is ArrayList -> collectionType
            is LinkedList -> collectionType
            is CopyOnWriteArrayList -> collectionType
            else -> throw RuntimeException("Unknown type of collection")
        }

        val executionTime = withContext(Dispatchers.Default) {
            val startTime = System.currentTimeMillis()
            collection.add(BEGINNING_INDEX, DIGIT_TO_ADD)
            val finishTime = System.currentTimeMillis()
            (finishTime - startTime).toString()
        }

        setResult(collection, executionTime)
    }

    private suspend fun addingInTheMiddleExecutionTime(collectionType: Collection<Int>) {
        val collection = when (collectionType) {
            is ArrayList -> collectionType
            is LinkedList -> collectionType
            is CopyOnWriteArrayList -> collectionType
            else -> throw RuntimeException("Unknown type of collection")
        }

        val executionTime = withContext(Dispatchers.Default) {
            val startTime = System.currentTimeMillis()
            val middleIndex = collection.size / 2
            collection.add(middleIndex, DIGIT_TO_ADD)
            val finishTime = System.currentTimeMillis()
            (finishTime - startTime).toString()
        }

        setResult(collection, executionTime)
    }

    private suspend fun addingInTheEndExecutionTime(collectionType: Collection<Int>) {
        val collection = when (collectionType) {
            is ArrayList -> collectionType
            is LinkedList -> collectionType
            is CopyOnWriteArrayList -> collectionType
            else -> throw RuntimeException("Unknown type of collection")
        }

        val executionTime = withContext(Dispatchers.Default) {
            val startTime = System.currentTimeMillis()
            collection.add(collection.lastIndex, DIGIT_TO_ADD)
            val finishTime = System.currentTimeMillis()
            (finishTime - startTime).toString()
        }

        setResult(collection, executionTime)
    }

    private fun setResult(collection: Collection<Int>, executionTime: String) {
        when (collection) {
            is ArrayList<*> -> {
                setExecutionTime(
                    CollectionsName.ARRAY_LIST.collectionName,
                    OperationsName.ADDING_IN_THE_BEGINNING.operationName,
                    executionTime
                )
            }
            is LinkedList<*> -> {
                setExecutionTime(
                    CollectionsName.LINKED_LIST.collectionName,
                    OperationsName.ADDING_IN_THE_BEGINNING.operationName,
                    executionTime
                )
            }
            is CopyOnWriteArrayList<*> -> {
                setExecutionTime(
                    CollectionsName.COPY_ON_WRITE_ARRAY_LIST.collectionName,
                    OperationsName.ADDING_IN_THE_BEGINNING.operationName,
                    executionTime
                )
            }
        }
    }

    private fun setExecutionTime(collectionsName: String, operationName: String, executionTime: String) {
        val index = operations.indexOfFirst {
            it.operationName == operationName && it.collectionName == collectionsName
        }
        if (index == UNDEFINED) return
        val updateOperation = operations[index].copy(averageExecutionTime = executionTime)
        operations[index] = updateOperation
        updateList()
    }

    private fun updateList() {
        operations = ArrayList(operations)
    }

    fun addListListeners(listener: ListOperationListener) {
        listeners.add(listener)
        listener.invoke(operations)
    }

    fun removeListListeners(listener: ListOperationListener) {
        listeners.remove(listener)
    }

    private fun notifyListChanges() {
        listeners.forEach { it.invoke(operations) }
    }

    companion object {
        private const val DIGIT_TO_FILL = 0
        private const val DIGIT_TO_ADD = 1
        private const val BEGINNING_INDEX = 0
        private const val UNDEFINED = -1
    }

}