package com.repedelano.repositories

import com.repedelano.dtos.vacancy.VacancyRequest
import com.repedelano.dtos.vacancy.VacancyStatus
import com.repedelano.orm.projrctroles.ProjectRoles
import com.repedelano.orm.technology.Technologies
import com.repedelano.orm.vacancies.Vacancies
import com.repedelano.orm.vacancies.VacancyTechnologies
import com.repedelano.resultOf
import com.repedelano.utils.db.DbTransaction
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.insertIgnoreAndGetId
import org.jetbrains.exposed.sql.select
import java.util.UUID

interface VacancyRepository {

    suspend fun add(vacancy: VacancyRequest): Result<Int?>
    suspend fun getById(id: Int): Result<ResultRow?>
    suspend fun getByIdeaId(id: UUID): Result<List<ResultRow>>
    suspend fun getByTechStack(technologies: List<String>): Result<List<ResultRow>>
}

class VacancyRepositoryImpl(private val dbTransaction: DbTransaction) : VacancyRepository {

    override suspend fun add(vacancy: VacancyRequest): Result<Int?> {
        return dbTransaction.dbQuery {
            resultOf {
                val projectRoleId = ProjectRoles.select(ProjectRoles.name eq vacancy.projectRole)
                    .first()[ProjectRoles.id]
                Vacancies.insertIgnoreAndGetId {
                    it[ideaId] = vacancy.ideaId
                    it[projectRole] = projectRoleId
                    it[description] = vacancy.description
                    it[status] = VacancyStatus.OPEN
                }?.value
            }
        }
    }

    override suspend fun getById(id: Int): Result<ResultRow?> {
        return dbTransaction.dbQuery {
            resultOf {
                Vacancies.select(Vacancies.id eq id) .firstOrNull()
            }
        }
    }

    override suspend fun getByIdeaId(id: UUID): Result<List<ResultRow>> {
        return dbTransaction.dbQuery {
            resultOf {
                Vacancies.select(Vacancies.ideaId eq id).toList()
            }
        }
    }

    override suspend fun getByTechStack(technologies: List<String>): Result<List<ResultRow>> {
        return dbTransaction.dbQuery {
            resultOf {
                val technologyIds = Technologies.select(Technologies.value inList technologies)
                    .map { it[Technologies.id].value }
                val vacancyIds = VacancyTechnologies.select(VacancyTechnologies.technologyId inList technologyIds)
                    .map { it[VacancyTechnologies.vacancyId].value }
                Vacancies.select(Vacancies.id inList vacancyIds).toList()
            }
        }
    }
}