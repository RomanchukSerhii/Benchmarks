package com.example.benchmarks.model


import com.example.benchmarks.model.enums.MapsName
import com.example.benchmarks.model.enums.MapsOperations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

typealias MapsOperationsListener = (operations: List<Operation>) -> Unit
class MapsOperationsService {

    private var operations = mutableListOf<Operation>()
    private val listeners = mutableSetOf<ListOperationsListener>()
    private val myCoroutineScope = CoroutineScope(Dispatchers.Default)

    init {
        for (map in MapsName.values()) {
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
}