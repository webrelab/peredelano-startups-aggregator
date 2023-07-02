package com.repedelano.services

import com.repedelano.dtos.scope.ScopeRequest
import com.repedelano.dtos.scope.ScopeResponse
import com.repedelano.dtos.scope.ScopeResponseList
import com.repedelano.flatMap
import com.repedelano.orm.helpers.toScope
import com.repedelano.orm.helpers.toScopeResponseList
import com.repedelano.repositories.ScopeRepository

interface ScopeService {

    suspend fun addIfNotExists(scope: ScopeRequest): Result<ScopeResponse?>
    suspend fun selectById(id: Int): Result<ScopeResponse?>
    suspend fun selectByName(name: String): Result<ScopeResponse?>
    suspend fun selectAll(): Result<ScopeResponseList>
    suspend fun update(scopeId: Int, scope: ScopeRequest): Result<Boolean>
}

class ScopeServiceImpl(private val scopeRepository: ScopeRepository) : ScopeService {

    override suspend fun addIfNotExists(scope: ScopeRequest) =
        scopeRepository.addIfNotExists(scope).flatMap { selectById(it!!) }

    override suspend fun selectById(id: Int) =
        scopeRepository.selectById(id).map { it!!.toScope() }

    override suspend fun selectByName(name: String) =
        scopeRepository.selectByName(name).map { it!!.toScope() }

    override suspend fun selectAll() =
        scopeRepository.selectAll().map { list -> list.map { it.toScope() }.toScopeResponseList() }

    override suspend fun update(scopeId: Int, scope: ScopeRequest) = scopeRepository.update(scopeId, scope)
}