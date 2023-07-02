package com.repedelano.utils.db

import kotlinx.coroutines.CoroutineDispatcher
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class DbTransaction(private val coroutineDispatcher: CoroutineDispatcher) {

    suspend fun <T> dbQuery(block: () -> T): T =
        newSuspendedTransaction(coroutineDispatcher) { block() }
}