package com.example.benchmarks.model

data class Operation (
    val operationName: String,
    val collectionName: String,
    var averageExecutionTime: Int
)