package com.repedelano.repositories

import com.repedelano.orm.idea.IdeaScopes
import com.repedelano.orm.scope.Scopes
import com.repedelano.resultOf
import com.repedelano.utils.db.DbTransaction
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.util.UUID

interface IdeaScopesRepository {

    suspend fun add(ideaId: UUID, scopeId: Int): Result<Boolean>
    suspend fun add(ideaId: UUID, scopeName: String): Result<Boolean>
    suspend fun selectBuIdeaId(ideaId: UUID): Result<List<ResultRow>>
    suspend fun delete(ideaId: UUID): Result<Boolean>
}

class IdeaScopesRepositoryImpl(private val dbTransaction: DbTransaction) : IdeaScopesRepository {

    override suspend fun add(ideaId: UUID, scopeId: Int): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                IdeaScopes.insert {
                    it[IdeaScopes.ideaId] = ideaId
                    it[IdeaScopes.scopeId] = scopeId
                }.resultedValues != null
            }
        }
    }

    override suspend fun add(ideaId: UUID, scopeName: String): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                IdeaScopes.insert {
                    it[IdeaScopes.ideaId] = ideaId
                    it[scopeId] = Scopes.slice(Scopes.id)
                        .select(Scopes.value eq scopeName)
                        .map { row -> row[Scopes.id] }.firstOrNull()
                        ?: throw java.lang.IllegalArgumentException("$scopeName not found")
                }.resultedValues != null
            }
        }
    }

    override suspend fun selectBuIdeaId(ideaId: UUID): Result<List<ResultRow>> {
        return dbTransaction.dbQuery {
            resultOf {
                IdeaScopes.join(Scopes, JoinType.INNER, onColumn = IdeaScopes.scopeId, otherColumn = Scopes.id)
                    .slice(Scopes.id, Scopes.value, Scopes.description)
                    .select(IdeaScopes.ideaId eq ideaId)
                    .toList()
            }
        }
    }

    override suspend fun delete(ideaId: UUID): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                IdeaScopes.deleteWhere { IdeaScopes.ideaId eq ideaId } > 0
            }
        }
    }
}