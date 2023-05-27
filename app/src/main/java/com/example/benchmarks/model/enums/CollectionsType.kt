package com.example.benchmarks.model.enums

enum class CollectionsType(val collectionName: String) {
    ARRAY_LIST("ArrayList"),
    LINKED_LIST("LinkedList"),
    COPY_ON_WRITE_ARRAY_LIST("CopyOnWriteArrayList")
}