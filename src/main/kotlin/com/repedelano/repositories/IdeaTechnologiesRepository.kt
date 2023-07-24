package com.repedelano.repositories

import com.repedelano.orm.idea.IdeaTechnologies
import com.repedelano.orm.technology.Technologies
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

interface IdeaTechnologiesRepository {

    suspend fun insert(ideaId: UUID, technologyIds: Collection<Int>): Result<Boolean>
    suspend fun selectByIdeaId(ideaId: UUID): Result<List<ResultRow>>
    suspend fun delete(ideaId: UUID): Result<Boolean>
    suspend fun delete(ideaId: UUID, technologyIds: Collection<Int>): Result<Boolean>
}

class IdeaTechnologiesRepositoryImpl(private val dbTransaction: DbTransaction) : IdeaTechnologiesRepository {

    override suspend fun insert(ideaId: UUID, technologyIds: Collection<Int>): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                IdeaTechnologies.batchInsert(technologyIds) {technologyId ->
                    this[IdeaTechnologies.ideaId] = ideaId
                    this[IdeaTechnologies.technologyId] = technologyId
                }.isNotEmpty()
            }
        }
    }

    override suspend fun selectByIdeaId(ideaId: UUID): Result<List<ResultRow>> {
        return dbTransaction.dbQuery {
            resultOf {
                IdeaTechnologies.join(
                    Technologies,
                    joinType = JoinType.INNER,
                    onColumn = IdeaTechnologies.technologyId,
                    otherColumn = Technologies.id
                )
                    .slice(Technologies.id, Technologies.value)
                    .select(IdeaTechnologies.ideaId eq ideaId)
                    .toList()
            }
        }
    }

    override suspend fun delete(ideaId: UUID): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                IdeaTechnologies.deleteWhere {
                    IdeaTechnologies.ideaId eq ideaId
                } > 0
            }
        }
    }

    override suspend fun delete(ideaId: UUID, technologyIds: Collection<Int>): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                technologyIds.map { technologyId ->
                    IdeaTechnologies.deleteWhere {
                        IdeaTechnologies.ideaId eq ideaId and
                            (IdeaTechnologies.technologyId eq technologyId)
                    }
                }.isNotEmpty()
            }
        }
    }

}