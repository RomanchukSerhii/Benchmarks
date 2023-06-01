package com.example.benchmarks.model


import android.util.Log
import com.example.benchmarks.ExecutingListener
import com.example.benchmarks.MapsOperationsListener
import com.example.benchmarks.model.enums.MapsType
import com.example.benchmarks.model.enums.MapsOperations
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MapsOperationsService {

    private var operations = mutableListOf<Operation>()
    private val mapsOperationsListeners = mutableSetOf<MapsOperationsListener>()
    private val listExecutingListeners = mutableSetOf<ExecutingListener>()
    private val myCoroutineScope = CoroutineScope(Dispatchers.Default)
    private val hashMapLock = Any()
    private val sortedMapLock = Any()
    private var hashMap = HashMap<Int, Int>(10000000)
    private var treeMap = TreeMap<Int, Int>()
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

    suspend fun startOperations(size: Int) {
        startExecute()

        mainJob = myCoroutineScope.launch {
            val sortedMapType = MapsType.SORTED_MAP
            val hashMapType = MapsType.HASH_MAP
            fillMaps(size)
            launch { addingNewExecutionTime(sortedMapType) }
            launch { searchByKeyExecutionTime(sortedMapType) }
            launch { removingExecutionTime(sortedMapType) }

            launch { addingNewExecutionTime(hashMapType) }
            launch { searchByKeyExecutionTime(hashMapType) }
            launch { removingExecutionTime(hashMapType) }
        }

        mainJob?.invokeOnCompletion {
            notifyExecutingChanges(false)
            hashMap.clear()
            treeMap.clear()
        }
    }

    private suspend fun addingNewExecutionTime(mapsType: MapsType) {
        val operationName = MapsOperations.ADDING_NEW
        getExecutionTime(mapsType, operationName)
    }

    private suspend fun searchByKeyExecutionTime(mapsType: MapsType) {
        val operationName = MapsOperations.SEARCH_BY_KEY
        getExecutionTime(mapsType, operationName)
    }

    private suspend fun removingExecutionTime(mapsType: MapsType) {
        val operationName = MapsOperations.REMOVING
        getExecutionTime(mapsType, operationName)
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

    private suspend fun getExecutionTime(
        mapsType: MapsType,
        operationName: MapsOperations
    ) {
        val executionTime = when (mapsType) {
            MapsType.SORTED_MAP -> getSortedMapExecutionTime(operationName)
            MapsType.HASH_MAP -> getHashMapExecutionTime(operationName)
        }
        setResult(mapsType, operationName, executionTime)
    }

    private fun getSortedMapExecutionTime(operationName: MapsOperations): String {
        if (mainJob?.isActive == true) {
            synchronized(sortedMapLock) {
                val startTime = System.currentTimeMillis()
                with(treeMap) {
                    when (operationName) {
                        MapsOperations.ADDING_NEW -> put(NEW_KEY, NEW_VALUE)
                        MapsOperations.SEARCH_BY_KEY -> get(DESIRED_KEY)
                        MapsOperations.REMOVING -> remove(DESIRED_KEY)
                    }
                }
                val finishTime = System.currentTimeMillis()
                return (finishTime - startTime).toString()
            }
        } else {
            throw CancellationException()
        }
    }

    private fun getHashMapExecutionTime(operationName: MapsOperations): String {
        if (mainJob?.isActive == true) {
            synchronized(hashMapLock) {
                val startTime = System.currentTimeMillis()
                with(hashMap) {
                    when (operationName) {
                        MapsOperations.ADDING_NEW -> put(NEW_KEY, NEW_VALUE)
                        MapsOperations.SEARCH_BY_KEY -> get(DESIRED_KEY)
                        MapsOperations.REMOVING -> remove(DESIRED_KEY)
                    }
                }
                val finishTime = System.currentTimeMillis()
                Log.d("AAA", "hash work - $operationName, size - ${hashMap.size}")
                return (finishTime - startTime).toString()
            }
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

    private fun fillMaps(size: Int) {
        for (i in 0 until size) {
            hashMap[i] = i
        }
        Log.d("AAA", "Hash - ${hashMap.size}")
        treeMap = TreeMap(hashMap)
        Log.d("AAA", "Sorted - ${treeMap.size}")
    }

    suspend fun cancelCoroutine() {
        mainJob?.cancel()
        mainJob?.join()
        stopExecute()
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
        private const val NEW_KEY = 11000000
        private const val NEW_VALUE = 13
        private const val DESIRED_KEY = 500000
    }
}