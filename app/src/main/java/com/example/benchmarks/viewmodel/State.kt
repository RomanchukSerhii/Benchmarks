package com.example.benchmarks.viewmodel

import com.example.benchmarks.model.Operation

sealed class State

class Error(val size: Int) : State()
class Executing(val isExecuting: Boolean) : State()
class Result(val operations: List<Operation>) : State()
class CollectionsSize(val size: Int) : State()