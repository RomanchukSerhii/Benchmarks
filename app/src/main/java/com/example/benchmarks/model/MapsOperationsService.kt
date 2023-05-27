package com.example.benchmarks.model


import com.example.benchmarks.ExecutingListener
import com.example.benchmarks.MapsOperationsListener
import com.example.benchmarks.model.enums.CollectionOperations
import com.example.benchmarks.model.enums.MapsType
import com.example.benchmarks.model.enums.MapsOperations
import kotlinx.coroutines.*
import java.util.TreeMap

class MapsOperationsService {

    private var operations = mutableListOf<Operation>()
    private val mapsOperationsListeners = mutableSetOf<MapsOperationsListener>()
    private val listExecutingListeners = mutableSetOf<ExecutingListener>()
    private val myCoroutineScope = CoroutineScope(Dispatchers.Default)
    private var mainJob: Job? = null

    init {
        for (map in MapsType.values()) {
            for (operation in MapsOperations.values()) {
                operations.add(
                    Operation(
                        operation.operationName,
                        map.mapsName,
                        "0",
                        false
                    )
                )
            }
        }
    }

    fun startOperations(size: Int) {
        startExecute()
        mainJob = myCoroutineScope.launch {
            val treeMapType = MapsType.TREE_MAP
            val hashMapType = MapsType.HASH_MAP

            launch { addingNewExecutionTime(treeMapType, size) }
            launch { searchByKeyExecutionTime(treeMapType, size) }
            launch { removingExecutionTime(treeMapType, size) }

            launch { addingNewExecutionTime(hashMapType, size) }
            launch { searchByKeyExecutionTime(hashMapType, size) }
            launch { removingExecutionTime(hashMapType, size) }
        }

        mainJob?.invokeOnCompletion {
            notifyExecutingChanges(false)
        }
    }

    private suspend fun addingNewExecutionTime(mapsType: MapsType, size: Int) {
        val operationName = MapsOperations.ADDING_NEW
        val executionTime = getExecutionTime(mapsType, operationName, size)
        setResult(mapsType, operationName, executionTime)
    }

    private suspend fun searchByKeyExecutionTime(mapsType: MapsType, size: Int) {

    }

    private suspend fun removingExecutionTime(mapsType: MapsType, size: Int) {

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

    private fun getExecutionTime(
        mapsType: MapsType,
        operationName: MapsOperations,
        size: Int
    ): String {
        if (mainJob?.isActive == true) {
            val collection = when (mapsType) {
                MapsType.TREE_MAP -> fillTreeMap(size)
                MapsType.HASH_MAP -> fillHashMap(size)
            }

            val startTime = System.currentTimeMillis()
            with(collection) {
                when (operationName) {
                    MapsOperations.ADDING_NEW -> TODO()
                    MapsOperations.SEARCH_BY_KEY -> TODO()
                    MapsOperations.REMOVING -> TODO()
                }
            }
            val finishTime = System.currentTimeMillis()
            return (finishTime - startTime).toString()
        } else {
            throw CancellationException()
        }
    }

    private suspend fun setResult(
        mapsType: MapsType,
        operationName: MapsOperations,
        executionTime: String
    ) {
        withContext(Dispatchers.Main) {
            updateList()
            val collectionName = mapsType.mapsName
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

    private fun fillTreeMap(size: Int): TreeMap<String, Int> {
        TODO()
    }

    private fun fillHashMap(size: Int): HashMap<String, Int> {
        TODO()
    }

    fun cancelCoroutine() {
        stopExecute()
        mainJob?.cancel()
    }
    fun addListListeners(listener: MapsOperationsListener) {
        mapsOperationsListeners.add(listener)
        listener.invoke(operations)
    }

    fun addExecutingListeners(listener: ExecutingListener) {
        listExecutingListeners.add(listener)
        listener.invoke(false)
    }

    fun removeListListeners(listener: MapsOperationsListener) {
        mapsOperationsListeners.remove(listener)
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
        mapsOperationsListeners.forEach { it.invoke(operations) }
    }

    companion object {
        private const val UNDEFINED = -1
    }
}