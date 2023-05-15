package com.example.benchmarks.model

import com.example.benchmarks.model.enums.CollectionsName
import com.example.benchmarks.model.enums.OperationsName

typealias ListOperationListener = (operations: List<Operation>) -> Unit
typealias MapOperationListener = (operations: List<Operation>) -> Unit

class OperationsService {
    private var operationsOnList = mutableListOf<Operation>()
    private val operationsOnMap = mutableListOf<Operation>()
    
    private val listListeners = mutableSetOf<ListOperationListener>()
    private val mapListeners = mutableSetOf<MapOperationListener>()    

    init {
        for (collection in CollectionsName.values()) {
            for (operation in OperationsName.values()) {
                operationsOnList.add(
                    Operation(
                        operation.operationName,
                        collection.collectionName,
                        0,
                        false
                    )
                )
            }
        }
    }

    fun addListListeners(listener: ListOperationListener) {
        listListeners.add(listener)
        listener.invoke(operationsOnList)
    }

    fun removeListListeners(listener: ListOperationListener) {
        listListeners.remove(listener)
    }

    fun addMapListeners(listener: MapOperationListener) {
        mapListeners.add(listener)
        listener.invoke(operationsOnList)
    }

    fun removeMapListeners(listener: MapOperationListener) {
        mapListeners.remove(listener)
    }
    
    fun startOperations() {
        operationsOnList = ArrayList(operationsOnList)
        for (index in 0 until operationsOnList.size) {
            val updateOperation = operationsOnList[index].copy(isExecuted = true)
            operationsOnList[index] = updateOperation
        }
        notifyListChanges()
    }
    
    private fun notifyListChanges() {
        listListeners.forEach { it.invoke(operationsOnList) }
    }

    fun getOperationList(): List<Operation> {
        return operationsOnList.toList()
    }
}