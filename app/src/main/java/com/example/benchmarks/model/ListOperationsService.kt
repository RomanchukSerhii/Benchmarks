package com.example.benchmarks.model

import android.util.Log
import com.example.benchmarks.ExecutingListener
import com.example.benchmarks.ListOperationsListener
import com.example.benchmarks.di.annotations.ApplicationScope
import com.example.benchmarks.model.enums.CollectionsType
import com.example.benchmarks.model.enums.CollectionOperations
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import kotlin.collections.ArrayList

@ApplicationScope
class ListOperationsService @Inject constructor() {

    private var operations = mutableListOf<Operation>()
    private val listOperationsListeners = mutableSetOf<ListOperationsListener>()
    private val listExecutingListeners = mutableSetOf<ExecutingListener>()
    private val myCoroutineScope = CoroutineScope(Dispatchers.Default)
    private val arrayListLock = Any()
    private val linkedListLock = Any()
    private var arrayList: ArrayList<Int>? = null
    private var copyOnWriteArrayList: CopyOnWriteArrayList<Int>? = null
    private var linkedList: LinkedList<Int>? = null
    private var mainJob: Job? = null
    private var collectionsSize = 0

    init {
        for (collection in CollectionsType.values()) {
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

    fun setCollectionsSize(size: Int) {
        collectionsSize = size
        notifyListChanges()
    }

    suspend fun startOperations() {
        startExecute()
        mainJob = myCoroutineScope.launch {
            val arrayListType = CollectionsType.ARRAY_LIST
            val linkedListType = CollectionsType.LINKED_LIST
            val collectionType = CollectionsType.COPY_ON_WRITE_ARRAY_LIST

            fillLists(collectionsSize)
            Log.d(TAG, "Array - ${arrayList?.size}")
            launch { addingInTheBeginningExecutionTime(arrayListType) }
            launch { addingInTheMiddleExecutionTime(arrayListType) }
            launch { addingInTheEndExecutionTime(arrayListType) }
            launch { removingInTheBeginningExecutionTime(arrayListType) }
            launch { removingInTheMiddleExecutionTime(arrayListType) }
            launch { removingInTheEndExecutionTime(arrayListType) }
            launch { searchByValueExecutionTime(arrayListType) }

            launch { addingInTheBeginningExecutionTime(collectionType) }
            launch { addingInTheMiddleExecutionTime(collectionType) }
            launch { addingInTheEndExecutionTime(collectionType) }
            launch { removingInTheBeginningExecutionTime(collectionType) }
            launch { removingInTheMiddleExecutionTime(collectionType) }
            launch { removingInTheEndExecutionTime(collectionType) }
            launch { searchByValueExecutionTime(collectionType) }

            launch { addingInTheBeginningExecutionTime(linkedListType) }
            launch { addingInTheMiddleExecutionTime(linkedListType) }
            launch { addingInTheEndExecutionTime(linkedListType) }
            launch { removingInTheBeginningExecutionTime(linkedListType) }
            launch { removingInTheMiddleExecutionTime(linkedListType) }
            launch { removingInTheEndExecutionTime(linkedListType) }
            launch { searchByValueExecutionTime(linkedListType) }
        }

        mainJob?.invokeOnCompletion {
            notifyExecutingChanges(false)
            arrayList?.clear()
            arrayList = null
            linkedList?.clear()
            linkedList = null
            copyOnWriteArrayList?.clear()
            copyOnWriteArrayList = null
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
    }

    private fun fillLists(size: Int) {
        arrayList = ArrayList(size)
        arrayList?.let {
            for (i in 0 until size) {
                if( i == size / 2) {
                    it.add(REQUIRED_VALUE)
                    continue
                }
                it.add(DIGIT_TO_FILL)
            }
            copyOnWriteArrayList = CopyOnWriteArrayList(it)
            linkedList = LinkedList(it)
        }

    }

    private suspend fun addingInTheBeginningExecutionTime(collectionType: CollectionsType) {
        val operationName = CollectionOperations.ADDING_IN_THE_BEGINNING
        getExecutionTime(collectionType, operationName)
    }

    private suspend fun addingInTheMiddleExecutionTime(collectionType: CollectionsType) {
        val operationName = CollectionOperations.ADDING_IN_THE_MIDDLE
        getExecutionTime(collectionType, operationName)
    }

    private suspend fun addingInTheEndExecutionTime(collectionType: CollectionsType) {
        val operationName = CollectionOperations.ADDING_IN_THE_END
        getExecutionTime(collectionType, operationName)
    }

    private suspend fun removingInTheBeginningExecutionTime(collectionType: CollectionsType) {
        val operationName = CollectionOperations.REMOVING_IN_THE_BEGINNING
        getExecutionTime(collectionType, operationName)
    }

    private suspend fun removingInTheMiddleExecutionTime(collectionType: CollectionsType) {
        val operationName = CollectionOperations.REMOVING_IN_THE_MIDDLE
        getExecutionTime(collectionType, operationName)
    }

    private suspend fun removingInTheEndExecutionTime(collectionType: CollectionsType) {
        val operationName = CollectionOperations.REMOVING_IN_THE_END
        getExecutionTime(collectionType, operationName)
    }

    private suspend fun searchByValueExecutionTime(collectionType: CollectionsType) {
        val operationName = CollectionOperations.SEARCH_BY_VALUE
        getExecutionTime(collectionType, operationName)
    }

    private suspend fun getExecutionTime(
        collectionType: CollectionsType,
        operationName: CollectionOperations
    ) {
        val executionTime = when (collectionType) {
            CollectionsType.ARRAY_LIST -> getArrayListExecutionTime(operationName)
            CollectionsType.LINKED_LIST -> getLinkedListExecutionTime(operationName)
            CollectionsType.COPY_ON_WRITE_ARRAY_LIST -> getCopyListExecutionTime(operationName)
        }

        setResult(collectionType, operationName, executionTime)
    }


    private fun getLinkedListExecutionTime(operationName: CollectionOperations): String {
        if (mainJob?.isActive == true) {
            synchronized(linkedListLock) {
                val startTime = System.currentTimeMillis()
                linkedList?.let {
                    val middleIndex = it.size / 2
                    val lastIndex = it.size - 2
                    with(it) {
                        when (operationName) {
                            CollectionOperations.ADDING_IN_THE_BEGINNING -> add(
                                BEGINNING_INDEX,
                                DIGIT_TO_ADD
                            )
                            CollectionOperations.ADDING_IN_THE_MIDDLE -> add(middleIndex, DIGIT_TO_ADD)
                            CollectionOperations.ADDING_IN_THE_END -> add(DIGIT_TO_ADD)
                            CollectionOperations.REMOVING_IN_THE_BEGINNING -> removeAt(BEGINNING_INDEX)
                            CollectionOperations.REMOVING_IN_THE_MIDDLE -> removeAt(middleIndex)
                            CollectionOperations.REMOVING_IN_THE_END -> removeAt(lastIndex)
                            CollectionOperations.SEARCH_BY_VALUE -> find { value ->
                                value == REQUIRED_VALUE
                            }
                        }
                    }
                }
                val finishTime = System.currentTimeMillis()
                return (finishTime - startTime).toString()
            }
        } else {
            throw CancellationException()
        }
    }

    private fun getArrayListExecutionTime(operationName: CollectionOperations): String {
        if (mainJob?.isActive == true && arrayList != null) {
            synchronized(arrayListLock) {
                val startTime = System.currentTimeMillis()
                arrayList?.let {
                    val middleIndex = it.size / 2
                    val lastIndex = it.size - 2
                    with(it) {
                        when (operationName) {
                            CollectionOperations.ADDING_IN_THE_BEGINNING -> add(
                                BEGINNING_INDEX,
                                DIGIT_TO_ADD
                            )
                            CollectionOperations.ADDING_IN_THE_MIDDLE -> add(middleIndex,
                                DIGIT_TO_ADD
                            )
                            CollectionOperations.ADDING_IN_THE_END -> add(DIGIT_TO_ADD)
                            CollectionOperations.REMOVING_IN_THE_BEGINNING -> removeAt(
                                BEGINNING_INDEX
                            )
                            CollectionOperations.REMOVING_IN_THE_MIDDLE -> removeAt(middleIndex)
                            CollectionOperations.REMOVING_IN_THE_END -> removeAt(lastIndex)
                            CollectionOperations.SEARCH_BY_VALUE -> find { value ->
                                value == REQUIRED_VALUE
                            }
                        }
                    }
                }
                val finishTime = System.currentTimeMillis()
                return (finishTime - startTime).toString()
            }
        } else {
            throw CancellationException()
        }
    }

    private fun getCopyListExecutionTime(operationName: CollectionOperations): String {
        if (mainJob?.isActive == true) {
            val startTime = System.currentTimeMillis()
            copyOnWriteArrayList?.let {
                val middleIndex = it.size / 2
                val lastIndex = it.size - 2
                with(it) {
                    when (operationName) {
                        CollectionOperations.ADDING_IN_THE_BEGINNING -> add(
                            BEGINNING_INDEX,
                            DIGIT_TO_ADD
                        )
                        CollectionOperations.ADDING_IN_THE_MIDDLE -> add(middleIndex,
                            DIGIT_TO_ADD
                        )
                        CollectionOperations.ADDING_IN_THE_END -> add(DIGIT_TO_ADD)
                        CollectionOperations.REMOVING_IN_THE_BEGINNING -> removeAt(
                            BEGINNING_INDEX
                        )
                        CollectionOperations.REMOVING_IN_THE_MIDDLE -> removeAt(middleIndex)
                        CollectionOperations.REMOVING_IN_THE_END -> removeAt(lastIndex)
                        CollectionOperations.SEARCH_BY_VALUE -> find { value ->
                            value == REQUIRED_VALUE }
                    }
                }
            }
            val finishTime = System.currentTimeMillis()
            return (finishTime - startTime).toString()
        } else {
            throw CancellationException()
        }
    }

    private suspend fun setResult(
        collectionsType: CollectionsType,
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
        mainJob?.cancel()
        stopExecute()
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