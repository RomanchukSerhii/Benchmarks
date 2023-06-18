package com.example.benchmarks

import com.example.benchmarks.model.Operation

typealias ListOperationsListener = (operations: List<Operation>) -> Unit
typealias MapsOperationsListener = (operations: List<Operation>) -> Unit
typealias ExecutingListener = (isExecuting: Boolean) -> Unit