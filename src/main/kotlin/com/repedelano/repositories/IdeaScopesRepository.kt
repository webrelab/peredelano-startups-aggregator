package com.repedelano.repositories

import com.repedelano.orm.idea.IdeaScopes
import com.repedelano.orm.scope.Scopes
import com.repedelano.resultOf
import com.repedelano.utils.db.DbTransaction
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import java.util.UUID

interface IdeaScopesRepository {

    suspend fun insert(ideaId: UUID, scopeIds: Collection<Int>): Result<Boolean>
    suspend fun selectByIdeaId(ideaId: UUID): Result<List<ResultRow>>
    suspend fun delete(ideaId: UUID): Result<Boolean>
    suspend fun delete(ideaId: UUID, scopeIds: Collection<Int>): Result<Boolean>
}

class IdeaScopesRepositoryImpl(private val dbTransaction: DbTransaction) : IdeaScopesRepository {

    override suspend fun insert(ideaId: UUID, scopeIds: Collection<Int>): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                IdeaScopes.batchInsert(scopeIds) { scopeId ->
                    this[IdeaScopes.ideaId] = ideaId
                    this[IdeaScopes.scopeId] = scopeId
                }.isNotEmpty()
            }
        }
    }

    override suspend fun selectByIdeaId(ideaId: UUID): Result<List<ResultRow>> {
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

    override suspend fun delete(ideaId: UUID, scopeIds: Collection<Int>): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                scopeIds.map { scopeId ->
                    IdeaScopes.deleteWhere {
                        IdeaScopes.ideaId eq ideaId and
                            (IdeaScopes.scopeId eq scopeId)
                    }
                }.isNotEmpty()
            }
        }
    }
}