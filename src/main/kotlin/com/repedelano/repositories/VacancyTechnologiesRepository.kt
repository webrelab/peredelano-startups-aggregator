package com.repedelano.repositories

import com.repedelano.orm.technology.Technologies
import com.repedelano.orm.vacancies.VacancyTechnologies
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

interface VacancyTechnologiesRepository {

    suspend fun insert(vacancyId: Int, technologyId: Int): Result<Boolean>
    suspend fun insert(vacancyId: Int, technologyIds: Collection<Int>): Result<Boolean>
    suspend fun selectByVacancyId(vacancyId: Int): Result<List<ResultRow>>
    suspend fun delete(vacancyId: Int, technologyIds: Collection<Int>): Result<Boolean>
}

class VacancyTechnologiesRepositoryImpl(private val dbTransaction: DbTransaction) : VacancyTechnologiesRepository {

    override suspend fun insert(vacancyId: Int, technologyId: Int): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                VacancyTechnologies.insert {
                    it[VacancyTechnologies.vacancyId] = vacancyId
                    it[VacancyTechnologies.technologyId] = technologyId
                }.resultedValues != null
            }
        }
    }

    override suspend fun insert(vacancyId: Int, technologyIds: Collection<Int>): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                VacancyTechnologies.batchInsert(technologyIds) { technologyId ->
                    this[VacancyTechnologies.vacancyId] = vacancyId
                    this[VacancyTechnologies.technologyId] = technologyId
                }.isNotEmpty()
            }
        }
    }

    override suspend fun selectByVacancyId(vacancyId: Int): Result<List<ResultRow>> {
        return dbTransaction.dbQuery {
            resultOf {
                VacancyTechnologies.join(
                    Technologies,
                    JoinType.INNER,
                    VacancyTechnologies.technologyId,
                    Technologies.id
                ).slice(
                    Technologies.id,
                    Technologies.value
                ).select(VacancyTechnologies.vacancyId eq vacancyId)
                    .toList()
            }
        }
    }

    override suspend fun delete(vacancyId: Int, technologyIds: Collection<Int>): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                VacancyTechnologies.deleteWhere {
                    VacancyTechnologies.vacancyId eq vacancyId and
                        (technologyId inList technologyIds)
                } > 0
            }
        }
    }
}