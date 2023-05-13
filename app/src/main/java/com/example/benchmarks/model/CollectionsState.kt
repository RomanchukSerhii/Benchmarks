package com.example.benchmarks.model

sealed class CollectionsState()

object Progress : CollectionsState()
class ArrayListAddingInTheBeginning(
    val result: String
) : CollectionsState()
class ArrayListAddingInTheMiddle(
    val result: String
) : CollectionsState()
class ArrayListAddingInTheEnd(
    val result: String
) : CollectionsState()
class ArrayListAddingSearchByValue(
    val result: String
) : CollectionsState()
class ArrayListRemovingInTheBeginning(
    val result: String
) : CollectionsState()
class ArrayListRemovingInTheMiddle(
    val result: String
) : CollectionsState()
class ArrayListRemovingInTheEnd(
    val result: String
) : CollectionsState()

class LinkedListAddingInTheBeginning(
    val result: String
) : CollectionsState()
class LinkedListAddingInTheMiddle(
    val result: String
) : CollectionsState()
class LinkedListAddingInTheEnd(
    val result: String
) : CollectionsState()
class LinkedListAddingSearchByValue(
    val result: String
) : CollectionsState()
class LinkedListRemovingInTheBeginning(
    val result: String
) : CollectionsState()
class LinkedListRemovingInTheMiddle(
    val result: String
) : CollectionsState()
class LinkedListRemovingInTheEnd(
    val result: String
) : CollectionsState()

class CopyOnWriteAddingInTheBeginning(
    val result: String
) : CollectionsState()
class CopyOnWriteAddingInTheMiddle(
    val result: String
) : CollectionsState()
class CopyOnWriteAddingInTheEnd(
    val result: String
) : CollectionsState()
class CopyOnWriteAddingSearchByValue(
    val result: String
) : CollectionsState()
class CopyOnWriteRemovingInTheBeginning(
    val result: String
) : CollectionsState()
class CopyOnWriteRemovingInTheMiddle(
    val result: String
) : CollectionsState()
class CopyOnWriteRemovingInTheEnd(
    val result: String
) : CollectionsState()