package com.repedelano.repositories

import com.repedelano.dtos.vacancy.VacancyRequest
import com.repedelano.dtos.vacancy.VacancySearchQuery
import com.repedelano.dtos.vacancy.VacancyStatus
import com.repedelano.extensions.withList
import com.repedelano.orm.vacancies.Vacancies
import com.repedelano.orm.vacancies.VacancyTechnologies
import com.repedelano.resultOf
import com.repedelano.utils.db.DbTransaction
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.util.UUID

interface VacancyRepository {

    suspend fun insert(vacancy: VacancyRequest): Result<Int?>

    suspend fun selectById(id: Int): Result<ResultRow?>
    suspend fun search(query: VacancySearchQuery): Result<List<ResultRow>>
    suspend fun selectByIdeaId(id: UUID): Result<List<ResultRow>>
    suspend fun selectAll(): Result<List<ResultRow>>
    suspend fun update(id: Int, vacancy: VacancyRequest): Result<Boolean>
    suspend fun updateStatus(id: Int, status: VacancyStatus): Result<Boolean>
}

class VacancyRepositoryImpl(private val dbTransaction: DbTransaction) : VacancyRepository {

    override suspend fun insert(vacancy: VacancyRequest): Result<Int?> {
        return dbTransaction.dbQuery {
            resultOf {
                Vacancies.insertAndGetId {
                    it[ideaId] = vacancy.ideaId
                    it[projectRoleId] = vacancy.projectRoleId
                    it[description] = vacancy.description
                    it[status] = VacancyStatus.OPEN
                }.value
            }
        }
    }

    override suspend fun selectById(id: Int): Result<ResultRow?> {
        return dbTransaction.dbQuery {
            resultOf {
                Vacancies.select(Vacancies.id eq id).firstOrNull()
            }
        }
    }

    override suspend fun search(query: VacancySearchQuery): Result<List<ResultRow>> {
        return dbTransaction.dbQuery {
            resultOf {
                val conditions = mutableListOf<Op<Boolean>>()
                query.ideaId?.let {
                    conditions.add(Vacancies.ideaId eq it)
                }
                query.projectRole?.let {
                    conditions.add(Vacancies.projectRoleId eq it)
                }
                query.description?.let {
                    conditions.add(Vacancies.description.lowerCase() like "%${it.lowercase()}%")
                }
                if (query.techStack.isNotEmpty()) {
                    val ids = query.techStack.map { technology ->
                        VacancyTechnologies.select(VacancyTechnologies.technologyId eq technology)
                            .map { it[VacancyTechnologies.vacancyId].value }
                    }.ifEmpty { listOf(emptyList()) }.reduce { acc, list -> acc.withList(list) }
                    conditions.add(Vacancies.id inList ids)
                }
                Vacancies.select(
                    conditions.reduce { acc, op -> acc and op }
                ).toMutableList()
            }
        }
    }

    override suspend fun selectByIdeaId(id: UUID): Result<List<ResultRow>> {
        return dbTransaction.dbQuery {
            resultOf {
                Vacancies.select(Vacancies.ideaId eq id).toList()
            }
        }
    }

    override suspend fun selectAll(): Result<List<ResultRow>> {
        return dbTransaction.dbQuery {
            resultOf {
                Vacancies.selectAll().toList()
            }
        }
    }

    override suspend fun update(id: Int, vacancy: VacancyRequest): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                Vacancies.update({ Vacancies.id eq id }) {
                    it[ideaId] = vacancy.ideaId
                    it[description] = vacancy.description
                    it[projectRoleId] = vacancy.projectRoleId
                } > 0
            }
        }
    }

    override suspend fun updateStatus(id: Int, status: VacancyStatus): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                Vacancies.update({ Vacancies.id eq id }) {
                    it[Vacancies.status] = status
                } > 0
            }
        }
    }
}