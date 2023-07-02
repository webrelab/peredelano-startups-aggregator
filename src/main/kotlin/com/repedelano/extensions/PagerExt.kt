package com.repedelano.extensions

import com.repedelano.dtos.Pager

fun Pager.offset() = page * itemsPerPage.toLong()

fun <T> List<T>.page(pager: Pager): List<T> {
    val listLastIndex = size - 1
    val offset = (pager.page * pager.itemsPerPage)
    val firstIndex = offset.coerceAtMost(listLastIndex)
    val lastIndex = (offset + pager.itemsPerPage).coerceAtMost(listLastIndex)
    return subList(firstIndex, lastIndex)
}