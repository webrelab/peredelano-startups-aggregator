package com.repedelano.repositories

import com.repedelano.dtos.idea.IdeaRequest
import com.repedelano.dtos.idea.IdeaStage
import com.repedelano.dtos.idea.IdeaStatus
import com.repedelano.orm.idea.Ideas
import com.repedelano.resultOf
import com.repedelano.utils.db.DbTransaction
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insertIgnoreAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.util.UUID

interface IdeaRepository {

    suspend fun insert(idea: IdeaRequest): Result<UUID?>
    suspend fun selectById(id: UUID): Result<ResultRow?>
    suspend fun selectAll(): Result<List<ResultRow>>
    suspend fun update(id: UUID, idea: IdeaRequest): Result<Boolean>
    suspend fun update(id: UUID, stage: IdeaStage): Result<Boolean>
    suspend fun update(id: UUID, status: IdeaStatus): Result<Boolean>
}

class IdeaRepositoryImpl(private val dbTransaction: DbTransaction) : IdeaRepository {

    override suspend fun insert(idea: IdeaRequest): Result<UUID?> {
        return dbTransaction.dbQuery {
            resultOf {
                val currentTime = Instant.now()
                Ideas.insertIgnoreAndGetId(createBody(idea) {
                    it[created] = currentTime
                    it[updated] = currentTime
                    it[stage] = IdeaStage.OPEN
                })?.value
            }
        }
    }

    override suspend fun selectById(id: UUID): Result<ResultRow?> {
        return dbTransaction.dbQuery {
            resultOf {
                Ideas.select(Ideas.id eq id).firstOrNull()
            }
        }
    }

    override suspend fun selectAll(): Result<List<ResultRow>> {
        return dbTransaction.dbQuery {
            resultOf {
                Ideas.selectAll()
                    .orderBy(Ideas.created)
                    .toList()
            }
        }
    }

    override suspend fun update(id: UUID, idea: IdeaRequest): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                Ideas.update({ Ideas.id eq id }, body = createBody(idea) {}) > 0
            }
        }
    }

    override suspend fun update(id: UUID, stage: IdeaStage): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                Ideas.update({ Ideas.id eq id}) {
                    it[Ideas.stage] = stage
                } > 0
            }
        }
    }

    override suspend fun update(id: UUID, status: IdeaStatus): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                Ideas.update({ Ideas.id eq id}) {
                    it[Ideas.status] = status
                } > 0
            }
        }
    }

    private fun <T> createBody(
        idea: IdeaRequest,
        additionalBody: Ideas.(UpdateBuilder<T>) -> Unit
    ): Ideas.(UpdateBuilder<T>) -> Unit {
        return {
            it[updated] = Instant.now()
            it[owner] = idea.owner
            it[title] = idea.title
            it[tgLink] = idea.tgLink
            it[boostyLink] = idea.boostyLink
            it[isFavorite] = false
            it[problem] = idea.problem
            it[description] = idea.description
            it[similarProjects] = idea.similarProjects
            it[targetAudience] = idea.targetAudience
            it[marketResearch] = idea.marketResearch
            it[businessPlan] = idea.businessPlan
            it[resources] = idea.resources
            additionalBody(it)
        }
    }
}
