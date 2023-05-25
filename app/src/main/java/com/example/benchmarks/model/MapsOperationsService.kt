package com.example.benchmarks.model

import com.example.benchmarks.model.enums.CollectionOperations
import com.example.benchmarks.model.enums.CollectionsName
import com.example.benchmarks.model.enums.MapsName
import com.example.benchmarks.model.enums.MapsOperations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.CopyOnWriteArrayList

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
}