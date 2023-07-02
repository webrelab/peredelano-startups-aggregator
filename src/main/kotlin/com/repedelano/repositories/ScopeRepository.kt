package com.repedelano.repositories

import com.repedelano.dtos.scope.ScopeRequest
import com.repedelano.orm.helpers.toScope
import com.repedelano.orm.scope.Scopes
import com.repedelano.resultOf
import com.repedelano.utils.db.DbTransaction
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insertIgnoreAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

interface ScopeRepository {

    suspend fun addIfNotExists(scope: ScopeRequest): Result<Int?>
    suspend fun selectById(id: Int): Result<ResultRow?>
    suspend fun selectByName(name: String): Result<ResultRow?>
    suspend fun selectAll(): Result<List<ResultRow>>
    suspend fun update(scopeId: Int, scope: ScopeRequest): Result<Boolean>
}

class ScopeRepositoryImpl(private val dbTransaction: DbTransaction) : ScopeRepository {

    override suspend fun addIfNotExists(scope: ScopeRequest): Result<Int?> {
        return dbTransaction.dbQuery {
            resultOf {
                Scopes.select(Scopes.value eq scope.value).map { it.toScope().id }.firstOrNull()
                    ?: Scopes.insertIgnoreAndGetId {
                        it[value] = scope.value
                        it[description] = scope.description
                    }?.value
            }
        }
    }

    override suspend fun selectById(id: Int): Result<ResultRow?> {
        return dbTransaction.dbQuery {
            resultOf {
                Scopes.select(Scopes.id eq id).firstOrNull()
            }
        }
    }

    override suspend fun selectByName(name: String): Result<ResultRow?> {
        return dbTransaction.dbQuery {
            resultOf {
                Scopes.select(Scopes.value eq name).firstOrNull()
            }
        }
    }

    override suspend fun selectAll(): Result<List<ResultRow>> {
        return dbTransaction.dbQuery {
            resultOf {
                Scopes.selectAll().toList()
            }
        }
    }

    override suspend fun update(scopeId: Int, scope: ScopeRequest): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                Scopes.update({ Scopes.id eq scopeId }) {
                    it[value] = scope.value
                    it[description] = scope.description
                } > 0
            }
        }
    }
}