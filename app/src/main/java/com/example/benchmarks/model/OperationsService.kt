package com.example.benchmarks.model

import com.example.benchmarks.model.enums.CollectionsName
import com.example.benchmarks.model.enums.OperationsName

class OperationsService {
    private val listOperations = mutableListOf<Operation>()
    private val mapOperations = mutableListOf<Operation>()

    init {
        for (collection in CollectionsName.values()) {
            for (operation in OperationsName.values()) {
                listOperations.add(
                    Operation(
                        operation.operationName,
                        collection.collectionName,
                        0
                    )
                )
            }
        }
    }

    fun getOperationList(): List<Operation> {
        return listOperations.toList()
    }
}