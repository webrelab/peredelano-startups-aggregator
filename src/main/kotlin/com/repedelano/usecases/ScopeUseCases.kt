package com.repedelano.usecases

import com.repedelano.dtos.scope.ScopeRequest
import com.repedelano.dtos.scope.ScopeResponse
import com.repedelano.dtos.scope.ScopeResponseList
import com.repedelano.services.ScopeService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

fun interface AddScopeUseCase {

    suspend fun add(scope: ScopeRequest): Result<ScopeResponse?>
}

fun interface GetScopeByIdUseCase {

    suspend fun get(id: Int): Result<ScopeResponse?>
}

fun interface SearchScopeUseCase {

    suspend fun search(query: String): Result<ScopeResponseList>
}

fun interface GetScopesUseCase {

    suspend fun getAll(): Result<ScopeResponseList>
}

fun interface UpdateScopeUseCase {

    suspend fun update(id: Int, scope: ScopeRequest): Result<ScopeResponse?>
}

fun addScopeUseCase(
    dispatcher: CoroutineDispatcher,
    scopeService: ScopeService
) = AddScopeUseCase { scope ->
    withContext(dispatcher) {
        scopeService.addIfNotExists(scope)
    }
}

fun getScopeByIdUseCase(
    dispatcher: CoroutineDispatcher,
    scopeService: ScopeService
) = GetScopeByIdUseCase { id ->
    withContext(dispatcher) {
        scopeService.selectById(id)
    }
}

fun searchScopeUseCase(
    dispatcher: CoroutineDispatcher,
    scopeService: ScopeService
) = SearchScopeUseCase { query ->
    withContext(dispatcher) {
        scopeService.search(query)
    }
}

fun getScopesUseCase(
    dispatcher: CoroutineDispatcher,
    scopeService: ScopeService
) = GetScopesUseCase {
    withContext(dispatcher) {
        scopeService.selectAll()
    }
}

fun updateScopeUseCase(
    dispatcher: CoroutineDispatcher,
    scopeService: ScopeService
) = UpdateScopeUseCase { id, scope ->
    withContext(dispatcher) {
        scopeService.update(id, scope)
    }
}