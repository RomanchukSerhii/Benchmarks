package com.example.benchmarks.model

import android.util.Log
import com.example.benchmarks.model.enums.CollectionsName
import com.example.benchmarks.model.enums.OperationsName
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.ArrayList

typealias ListOperationsListener = (operations: List<Operation>) -> Unit

class ListOperationsService {

    private var operations = mutableListOf<Operation>()
    private val listeners = mutableSetOf<ListOperationsListener>()
    private var copyOnWriteArrayList = CopyOnWriteArrayList<Int>()
    private val myCoroutineScope = CoroutineScope(Dispatchers.Default)

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

    suspend fun startOperations(size: Int) {
        startExecute()
        val fillListJob = myCoroutineScope.launch { fillCopyOnWriteArrayList(size) }

        myCoroutineScope.launch {
            launch {
                val arrayList = fillArrayList(size)
                addingInTheBeginningExecutionTime(arrayList)
            }
            launch {
                val linkedList = fillLinkedList(size)
                addingInTheBeginningExecutionTime(linkedList)
            }
            launch {
                val arrayList = fillArrayList(size)
                addingInTheMiddleExecutionTime(arrayList)

            }
            launch {
                val linkedList = fillLinkedList(size)
                addingInTheMiddleExecutionTime(linkedList)
            }
            launch {
                val arrayList = fillArrayList(size)
                addingInTheEndExecutionTime(arrayList)
            }
            launch {
                val linkedList = fillLinkedList(size)
                addingInTheEndExecutionTime(linkedList)
            }
            launch {
                val arrayList = fillArrayList(size)
                removingInTheBeginningExecutionTime(arrayList)
            }
            launch {
                val linkedList = fillLinkedList(size)
                removingInTheBeginningExecutionTime(linkedList)
            }
            launch {
                val arrayList = fillArrayList(size)
                removingInTheMiddleExecutionTime(arrayList)
            }
            launch {
                val linkedList = fillLinkedList(size)
                removingInTheMiddleExecutionTime(linkedList)
            }
            launch {
                val arrayList = fillArrayList(size)
                removingInTheEndExecutionTime(arrayList)
            }
            launch {
                val linkedList = fillLinkedList(size)
                removingInTheEndExecutionTime(linkedList)
            }
            launch {
                val arrayList = fillArrayList(size)
                searchByValueExecutionTime(arrayList)
            }
            launch {
                val linkedList = fillLinkedList(size)
                searchByValueExecutionTime(linkedList)
            }
        }

        fillListJob.join()
        myCoroutineScope.launch {
            launch { addingInTheBeginningExecutionTime(copyOnWriteArrayList) }
            launch { addingInTheMiddleExecutionTime(copyOnWriteArrayList) }
            launch { addingInTheEndExecutionTime(copyOnWriteArrayList) }
            launch { removingInTheBeginningExecutionTime(copyOnWriteArrayList) }
            launch { removingInTheMiddleExecutionTime(copyOnWriteArrayList) }
            launch { removingInTheEndExecutionTime(copyOnWriteArrayList) }
            launch { searchByValueExecutionTime(copyOnWriteArrayList) }
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

    private suspend fun addingInTheBeginningExecutionTime(collectionType: Collection<Int>) {
        val collection = when (collectionType) {
            is ArrayList -> collectionType
            is LinkedList -> collectionType
            is CopyOnWriteArrayList -> collectionType
            else -> throw RuntimeException("Unknown type of collection")
        }
        val collectionName = defineCollectionName(collection)

        val startTime = System.currentTimeMillis()
        collection.add(BEGINNING_INDEX, DIGIT_TO_ADD)
        val finishTime = System.currentTimeMillis()
        val executionTime = (finishTime - startTime).toString()

        setResult(
            collectionName,
            OperationsName.ADDING_IN_THE_BEGINNING.operationName,
            executionTime
        )
    }

    private suspend fun addingInTheMiddleExecutionTime(collectionType: Collection<Int>) {
        val collection = when (collectionType) {
            is ArrayList -> collectionType
            is LinkedList -> collectionType
            is CopyOnWriteArrayList -> collectionType
            else -> throw RuntimeException("Unknown type of collection")
        }
        val collectionName = defineCollectionName(collection)

        val startTime = System.currentTimeMillis()
        val middleIndex = collection.size / 2
        collection.add(middleIndex, DIGIT_TO_ADD)
        val finishTime = System.currentTimeMillis()
        val executionTime = (finishTime - startTime).toString()

        setResult(
            collectionName,
            OperationsName.ADDING_IN_THE_MIDDLE.operationName,
            executionTime
        )
    }

    private suspend fun addingInTheEndExecutionTime(collectionType: Collection<Int>) {
        val collection = when (collectionType) {
            is ArrayList -> collectionType
            is LinkedList -> collectionType
            is CopyOnWriteArrayList -> collectionType
            else -> throw RuntimeException("Unknown type of collection")
        }
        val collectionName = defineCollectionName(collection)

        val startTime = System.currentTimeMillis()
        collection.add(DIGIT_TO_ADD)
        val finishTime = System.currentTimeMillis()
        val executionTime = (finishTime - startTime).toString()

        setResult(
            collectionName,
            OperationsName.ADDING_IN_THE_END.operationName,
            executionTime
        )
    }

    private suspend fun removingInTheBeginningExecutionTime(collectionType: Collection<Int>) {
        val collection = when (collectionType) {
            is ArrayList -> collectionType
            is LinkedList -> collectionType
            is CopyOnWriteArrayList -> collectionType
            else -> throw RuntimeException("Unknown type of collection")
        }
        val collectionName = defineCollectionName(collection)

        val startTime = System.currentTimeMillis()
        collection.removeAt(BEGINNING_INDEX)
        val finishTime = System.currentTimeMillis()
        val executionTime = (finishTime - startTime).toString()

        setResult(
            collectionName,
            OperationsName.REMOVING_IN_THE_BEGINNING.operationName,
            executionTime
        )
    }

    private suspend fun removingInTheMiddleExecutionTime(collectionType: Collection<Int>) {
        val collection = when (collectionType) {
            is ArrayList -> collectionType
            is LinkedList -> collectionType
            is CopyOnWriteArrayList -> collectionType
            else -> throw RuntimeException("Unknown type of collection")
        }
        val collectionName = defineCollectionName(collection)

        val startTime = System.currentTimeMillis()
        val middleIndex = collection.size / 2
        collection.removeAt(middleIndex)
        val finishTime = System.currentTimeMillis()
        val executionTime = (finishTime - startTime).toString()

        setResult(
            collectionName,
            OperationsName.REMOVING_IN_THE_MIDDLE.operationName,
            executionTime
        )
    }

    private suspend fun removingInTheEndExecutionTime(collectionType: Collection<Int>) {
        val collection = when (collectionType) {
            is ArrayList -> collectionType
            is LinkedList -> collectionType
            is CopyOnWriteArrayList -> collectionType
            else -> throw RuntimeException("Unknown type of collection")
        }
        val collectionName = defineCollectionName(collection)

        val startTime = System.currentTimeMillis()
        collection.removeAt(collection.lastIndex)
        val finishTime = System.currentTimeMillis()
        val executionTime = (finishTime - startTime).toString()

        setResult(
            collectionName,
            OperationsName.REMOVING_IN_THE_END.operationName,
            executionTime
        )
    }

    private suspend fun searchByValueExecutionTime(collectionType: Collection<Int>) {
        val collection = when (collectionType) {
            is ArrayList -> collectionType
            is LinkedList -> collectionType
            is CopyOnWriteArrayList -> collectionType
            else -> throw RuntimeException("Unknown type of collection")
        }
        val collectionName = defineCollectionName(collection)

        val startTime = System.currentTimeMillis()
        collection.find { it == REQUIRED_VALUE }
        val finishTime = System.currentTimeMillis()
        val executionTime = (finishTime - startTime).toString()

        setResult(
            collectionName,
            OperationsName.SEARCH_BY_VALUE.operationName,
            executionTime
        )
    }

    private fun defineCollectionName(collection: Collection<Int>): String {
        return when (collection) {
            is ArrayList -> CollectionsName.ARRAY_LIST.collectionName
            is LinkedList -> CollectionsName.LINKED_LIST.collectionName
            is CopyOnWriteArrayList -> CollectionsName.COPY_ON_WRITE_ARRAY_LIST.collectionName
            else -> throw RuntimeException("Unknown type of collection")
        }
    }

    private suspend fun setResult(
        collectionsName: String,
        operationName: String,
        executionTime: String
    ) {
        withContext(Dispatchers.Main) {
            updateList()
            val index = operations.indexOfFirst {
                it.operationName == operationName && it.collectionName == collectionsName
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
        myCoroutineScope.cancel()
    }
    fun addListListeners(listener: ListOperationsListener) {
        listeners.add(listener)
        listener.invoke(operations)
    }

    fun removeListListeners(listener: ListOperationsListener) {
        listeners.remove(listener)
    }

    private fun updateList() {
        operations = ArrayList(operations)
    }

    private fun notifyListChanges() {
        listeners.forEach { it.invoke(operations) }
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