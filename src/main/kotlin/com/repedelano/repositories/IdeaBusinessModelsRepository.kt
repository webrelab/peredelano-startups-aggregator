package com.repedelano.repositories

import com.repedelano.orm.businessmodel.BusinessModels
import com.repedelano.orm.idea.IdeaBusinessModels
import com.repedelano.resultOf
import com.repedelano.utils.db.DbTransaction
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.util.UUID

interface IdeaBusinessModelsRepository {

    suspend fun insert(ideaId: UUID, businessModelId: Int): Result<Boolean>
    suspend fun insert(ideaId: UUID, businessModelName: String): Result<Boolean>
    suspend fun selectByIdeaId(ideaId: UUID): Result<List<ResultRow>>
    suspend fun delete(ideaId: UUID): Result<Boolean>
}

class IdeaBusinessModelsRepositoryImpl(private val dbTransaction: DbTransaction) : IdeaBusinessModelsRepository {

    override suspend fun insert(ideaId: UUID, businessModelId: Int): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                IdeaBusinessModels.insert {
                    it[IdeaBusinessModels.ideaId] = ideaId
                    it[IdeaBusinessModels.businessModelId] = businessModelId
                }.resultedValues != null
            }
        }
    }

    override suspend fun insert(ideaId: UUID, businessModelName: String): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                IdeaBusinessModels.insert {
                    it[IdeaBusinessModels.ideaId] = ideaId
                    it[businessModelId] = BusinessModels.slice(BusinessModels.id)
                        .select(BusinessModels.value eq businessModelName)
                        .map { row -> row[BusinessModels.id] }
                        .firstOrNull()
                        ?: throw java.lang.IllegalArgumentException("$businessModelName not found")
                }.resultedValues != null
            }
        }
    }

    override suspend fun selectByIdeaId(ideaId: UUID): Result<List<ResultRow>> {
        return dbTransaction.dbQuery {
            resultOf {
                IdeaBusinessModels.join(
                    BusinessModels,
                    joinType = JoinType.INNER,
                    onColumn = IdeaBusinessModels.businessModelId,
                    otherColumn = BusinessModels.id
                )
                    .slice(BusinessModels.id, BusinessModels.value, BusinessModels.description)
                    .select(IdeaBusinessModels.ideaId eq ideaId)
                    .toList()
            }
        }
    }

    override suspend fun delete(ideaId: UUID): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                IdeaBusinessModels.deleteWhere {
                    IdeaBusinessModels.ideaId eq ideaId
                } > 0
            }
        }
    }
}