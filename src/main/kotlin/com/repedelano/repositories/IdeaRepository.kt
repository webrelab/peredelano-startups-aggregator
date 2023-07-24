package com.repedelano.repositories

import com.repedelano.dtos.idea.IdeaRequest
import com.repedelano.dtos.idea.IdeaStage
import com.repedelano.dtos.idea.IdeaStatus
import com.repedelano.extensions.withList
import com.repedelano.orm.idea.IdeaBusinessModels
import com.repedelano.orm.idea.IdeaScopes
import com.repedelano.orm.idea.IdeaTechnologies
import com.repedelano.orm.idea.Ideas
import com.repedelano.resultOf
import com.repedelano.services.IdeaStatusHandler
import com.repedelano.utils.db.DbTransaction
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.or
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
    suspend fun search(
        owner: Int?,
        queryString: String?,
        scopes: List<Int>,
        businessModels: List<Int>,
        technologies: List<Int>,
    ): Result<List<ResultRow>>

    suspend fun update(id: UUID, idea: IdeaRequest): Result<Boolean>
    suspend fun update(id: UUID, stage: IdeaStage): Result<Boolean>
    suspend fun update(id: UUID, status: IdeaStatus): Result<Boolean>
}

class IdeaRepositoryImpl(private val dbTransaction: DbTransaction) : IdeaRepository {

    override suspend fun insert(idea: IdeaRequest): Result<UUID?> {
        return dbTransaction.dbQuery {
            resultOf {
                val currentTime = Instant.now()
                Ideas.insertAndGetId(createBody(idea) {
                    it[created] = currentTime
                    it[updated] = currentTime
                    it[isFavorite] = false
                    it[status] = IdeaStatusHandler.getStatus(idea)
                    it[stage] = IdeaStage.OPEN
                }).value
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

    override suspend fun search(
        owner: Int?,
        queryString: String?,
        scopes: List<Int>,
        businessModels: List<Int>,
        technologies: List<Int>,
    ): Result<List<ResultRow>> {
        return dbTransaction.dbQuery {
            resultOf {
                val conditions = mutableListOf<Op<Boolean>>()
                if (owner != null) {
                    conditions.add(Ideas.owner eq owner)
                }
                if (queryString != null) {
                    val query = "%${queryString.lowercase()}%"
                    val map: List<Op<Boolean>> = listOf(
                        Ideas.resources,
                        Ideas.businessPlan,
                        Ideas.title,
                        Ideas.marketResearch,
                        Ideas.targetAudience,
                        Ideas.similarProjects,
                        Ideas.problem,
                        Ideas.description,
                        Ideas.tgLink,
                    ).map { it.lowerCase() like query }
                    conditions.add(map.reduce { acc, op -> acc or op })
                }
                val idsByScopes = scopes.map { scope ->
                    IdeaScopes.select(IdeaScopes.scopeId eq scope)
                        .map { it[IdeaScopes.ideaId].value }
                }.ifEmpty { listOf(emptyList()) }.reduce { acc, list -> acc.withList(list) }

                val idsByBusinessModels =
                    businessModels.map { businessModel ->
                        IdeaBusinessModels.select(IdeaBusinessModels.businessModelId eq businessModel)
                            .map { it[IdeaBusinessModels.ideaId].value }
                    }.ifEmpty { listOf(emptyList()) }.reduce { acc, list -> acc.withList(list) }

                val idsByTechStack =
                    technologies.map { technology ->
                        IdeaTechnologies.select(IdeaTechnologies.technologyId eq technology)
                            .map { it[IdeaTechnologies.ideaId].value }

                    }.ifEmpty { listOf(emptyList()) }.reduce { acc, list -> acc.withList(list) }

                val ids = idsByScopes.withList(idsByBusinessModels)
                    .withList(idsByTechStack)
                if (ids.isNotEmpty()) {
                    conditions.add(Ideas.id inList ids)
                }
                if (conditions.isEmpty()) {
                    Ideas.selectAll().toList()
                } else {
                    Ideas.select(
                        conditions.reduce { acc, op -> acc and op }
                    ).toList()
                }
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
                Ideas.update({ Ideas.id eq id }) {
                    it[Ideas.stage] = stage
                } > 0
            }
        }
    }

    override suspend fun update(id: UUID, status: IdeaStatus): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                Ideas.update({ Ideas.id eq id }) {
                    it[Ideas.status] = status
                } > 0
            }
        }
    }

    private fun <T> createBody(
        idea: IdeaRequest,
        additionalBody: Ideas.(UpdateBuilder<T>) -> Unit
    ): Ideas.(UpdateBuilder<T>) -> Unit {
        return { builder ->
            builder[updated] = Instant.now()
            idea.owner?.let { builder[owner] = idea.owner }
            idea.title?.let { builder[title] = idea.title }
            idea.tgLink?.let { builder[tgLink] = idea.tgLink }
            idea.problem?.let { builder[problem] = idea.problem }
            idea.description?.let { builder[description] = idea.description }
            idea.similarProjects?.let { builder[similarProjects] = idea.similarProjects }
            idea.targetAudience?.let { builder[targetAudience] = idea.targetAudience }
            idea.marketResearch?.let { builder[marketResearch] = idea.marketResearch }
            idea.businessPlan?.let { builder[businessPlan] = idea.businessPlan }
            idea.resources?.let { builder[resources] = idea.resources }
            additionalBody(builder)
        }
    }
}
