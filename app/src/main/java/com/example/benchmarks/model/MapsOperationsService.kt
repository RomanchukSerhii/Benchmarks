package com.example.benchmarks.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.CopyOnWriteArrayList

typealias MapsOperationsListener = (operations: List<Operation>) -> Unit
class MapsOperationsService {

    private var operations = mutableListOf<Operation>()
    private val listeners = mutableSetOf<ListOperationsListener>()
    private val myCoroutineScope = CoroutineScope(Dispatchers.Default)
}