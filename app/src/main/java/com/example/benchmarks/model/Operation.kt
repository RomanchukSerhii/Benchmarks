package com.example.benchmarks.model

data class Operation (
    val operationName: String,
    val collectionName: String,
    val averageExecutionTime: String,
    val isExecuted: Boolean
)