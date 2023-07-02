package com.repedelano.repositories

import com.repedelano.orm.vacancies.VacancyTechnologies
import com.repedelano.resultOf
import com.repedelano.utils.db.DbTransaction
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

interface VacancyTechnologiesRepository {

    suspend fun add(vacancyId: Int, technologyId: Int): Result<Boolean>
    suspend fun selectByTechnologyId(technologyId: Int): Result<List<ResultRow>>
    suspend fun selectByTechnologyIds(technologyIds: List<Int>): Result<List<ResultRow>>
    suspend fun delete(vacancyId: Int): Result<Boolean>
}

class VacancyTechnologiesRepositoryImpl(private val dbTransaction: DbTransaction) : VacancyTechnologiesRepository {

    override suspend fun add(vacancyId: Int, technologyId: Int): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                VacancyTechnologies.insert {
                    it[VacancyTechnologies.vacancyId] = vacancyId
                    it[VacancyTechnologies.technologyId] = technologyId
                }.resultedValues != null
            }
        }
    }

    override suspend fun selectByTechnologyId(technologyId: Int): Result<List<ResultRow>> {
        return dbTransaction.dbQuery {
            resultOf {
                VacancyTechnologies.select(VacancyTechnologies.technologyId eq technologyId).toList()
            }
        }
    }

    override suspend fun selectByTechnologyIds(technologyIds: List<Int>): Result<List<ResultRow>> {
        return dbTransaction.dbQuery {
            resultOf {
                VacancyTechnologies.select(VacancyTechnologies.technologyId inList technologyIds).toList()
            }
        }
    }

    override suspend fun delete(vacancyId: Int): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                VacancyTechnologies.deleteWhere { VacancyTechnologies.vacancyId eq vacancyId } > 0
            }
        }
    }
}