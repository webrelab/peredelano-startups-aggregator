package com.repedelano.extensions

fun <T> List<T>.withList(other: List<T>): List<T> {
    return when {
        this is EmptySearchList -> this
        isEmpty() -> other
        other.isEmpty() -> this
        else -> other.filter { contains(it) }.ifEmpty { EmptySearchList() }
    }
}

class EmptySearchList<T> : kotlin.collections.ArrayList<T>()