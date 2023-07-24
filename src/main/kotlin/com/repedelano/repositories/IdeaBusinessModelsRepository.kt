package com.repedelano.repositories

import com.repedelano.orm.businessmodel.BusinessModels
import com.repedelano.orm.idea.IdeaBusinessModels
import com.repedelano.resultOf
import com.repedelano.utils.db.DbTransaction
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.util.UUID

interface IdeaBusinessModelsRepository {

    suspend fun insert(ideaId: UUID, businessModelIds: Collection<Int>): Result<Boolean>
    suspend fun selectByIdeaId(ideaId: UUID): Result<List<ResultRow>>
    suspend fun delete(ideaId: UUID): Result<Boolean>
    suspend fun delete(ideaId: UUID, businessModelIds: Collection<Int>): Result<Boolean>
}

class IdeaBusinessModelsRepositoryImpl(private val dbTransaction: DbTransaction) : IdeaBusinessModelsRepository {

    override suspend fun insert(ideaId: UUID, businessModelIds: Collection<Int>): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                // TODO нужно сделать настройку БД для правильной работы batchInsesrt https://github.com/JetBrains/Exposed/wiki/DSL#batch-insert
                IdeaBusinessModels.batchInsert(businessModelIds) { businessModelId ->
                    this[IdeaBusinessModels.ideaId] = ideaId
                    this[IdeaBusinessModels.businessModelId] = businessModelId
                }.isNotEmpty()
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

    override suspend fun delete(ideaId: UUID, businessModelIds: Collection<Int>): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
//                businessModelIds.map { businessModelId ->
//                    IdeaBusinessModels.deleteWhere {
//                        IdeaBusinessModels.ideaId eq ideaId and
//                            (IdeaBusinessModels.businessModelId eq businessModelId)
//                    }
//                }.isNotEmpty()
                IdeaBusinessModels.deleteWhere {
                    IdeaBusinessModels.ideaId eq ideaId and
                        (IdeaBusinessModels.businessModelId inList businessModelIds)
                } > 0
            }
        }
    }
}