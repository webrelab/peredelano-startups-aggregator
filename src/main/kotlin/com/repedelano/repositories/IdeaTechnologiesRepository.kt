package com.repedelano.repositories

import com.repedelano.orm.idea.IdeaTechnologies
import com.repedelano.orm.technology.Technologies
import com.repedelano.resultOf
import com.repedelano.utils.db.DbTransaction
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.util.UUID

interface IdeaTechnologiesRepository {

    suspend fun insert(ideaId: UUID, technologyId: Int): Result<Boolean>
    suspend fun insert(ideaId: UUID, technologyName: String): Result<Boolean>
    suspend fun selectByIdeaId(ideaId: UUID): Result<List<ResultRow>>
    suspend fun delete(ideaId: UUID): Result<Boolean>
}

class IdeaTechnologiesRepositoryImpl(private val dbTransaction: DbTransaction) : IdeaTechnologiesRepository {

    override suspend fun insert(ideaId: UUID, technologyId: Int): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                IdeaTechnologies.insert {
                    it[IdeaTechnologies.ideaId] = ideaId
                    it[IdeaTechnologies.technologyId] = technologyId
                }.resultedValues != null
            }
        }
    }

    override suspend fun insert(ideaId: UUID, technologyName: String): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                IdeaTechnologies.insert {
                    it[IdeaTechnologies.ideaId] = ideaId
                    it[technologyId] = Technologies.slice(Technologies.id)
                        .select(Technologies.value eq technologyName)
                        .map { row -> row[Technologies.id] }
                        .firstOrNull()
                        ?: throw java.lang.IllegalArgumentException("$technologyName not found")
                }.resultedValues != null
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

}