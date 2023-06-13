package com.example.benchmarks.model

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
    private var hashMap: HashMap<Int, Int>? = null
    private var treeMap: TreeMap<Int, Int>? = null
    private var mainJob: Job? = null
    private var collectionsSize = 0

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

    fun setCollectionsSize(size: Int) {
        collectionsSize = size
        notifyListChanges()
    }

    suspend fun startOperations() {
        startExecute()

        mainJob = myCoroutineScope.launch {
            val sortedMapType = MapsType.TREE_MAP
            val hashMapType = MapsType.HASH_MAP

            val hashJob = launch {
                fillHashMap(collectionsSize)
                launch { addingNewExecutionTime(hashMapType) }
                launch { searchByKeyExecutionTime(hashMapType) }
                launch { removingExecutionTime(hashMapType) }
            }

            val treeJob = launch {
                if (collectionsSize > 8_000_000) {
                    fillTreeMap(size = 2_000_000)
                    hashJob.join()
                    fillTreeMap(2_000_000, collectionsSize)
                } else {
                    fillTreeMap(size = collectionsSize)
                }
                launch { addingNewExecutionTime(sortedMapType) }
                launch { searchByKeyExecutionTime(sortedMapType) }
                launch { removingExecutionTime(sortedMapType) }
            }

            hashJob.invokeOnCompletion {
                hashMap?.clear()
                hashMap = null
            }
            treeJob.invokeOnCompletion {
                treeMap?.clear()
                treeMap = null
            }
        }

        mainJob?.invokeOnCompletion {
            notifyExecutingChanges(false)
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
            MapsType.TREE_MAP -> getSortedMapExecutionTime(operationName)
            MapsType.HASH_MAP -> getHashMapExecutionTime(operationName)
        }
        setResult(mapsType, operationName, executionTime)
    }

    private fun getSortedMapExecutionTime(operationName: MapsOperations): String {
        if (mainJob?.isActive == true) {
            synchronized(sortedMapLock) {
                val startTime = System.currentTimeMillis()
                treeMap?.let {
                    with(it) {
                        when (operationName) {
                            MapsOperations.ADDING_NEW -> put(NEW_KEY, NEW_VALUE)
                            MapsOperations.SEARCH_BY_KEY -> get(DESIRED_KEY)
                            MapsOperations.REMOVING -> remove(DESIRED_KEY)
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

    private fun getHashMapExecutionTime(operationName: MapsOperations): String {
        if (mainJob?.isActive == true) {
            synchronized(hashMapLock) {
                val startTime = System.currentTimeMillis()
                hashMap?.let {
                    with(it) {
                        when (operationName) {
                            MapsOperations.ADDING_NEW -> put(NEW_KEY, NEW_VALUE)
                            MapsOperations.SEARCH_BY_KEY -> get(DESIRED_KEY)
                            MapsOperations.REMOVING -> remove(DESIRED_KEY)
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

    private fun fillHashMap(size: Int) {
        hashMap = HashMap(size)
        hashMap?.let {
            for (i in 0 until size) {
                if (mainJob?.isActive == true) {
                    it[i] = i
                }
            }
        }
    }
    
    private fun fillTreeMap(startIndex: Int = 0, size: Int) {
        if (treeMap == null) {
            treeMap = TreeMap()
            treeMap?.let {
                for (i in startIndex until size) {
                    if (mainJob?.isActive == true) {
                        it[i] = i
                    }
                }
            }
        } else {
            treeMap?.let {
                for (i in startIndex until size) {
                    if (mainJob?.isActive == true) {
                        it[i] = i
                    }
                }
            }
        }
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