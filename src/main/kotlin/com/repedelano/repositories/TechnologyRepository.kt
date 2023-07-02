package com.repedelano.repositories

import com.repedelano.dtos.technology.TechnologyRequest
import com.repedelano.orm.technology.Technologies
import com.repedelano.resultOf
import com.repedelano.utils.db.DbTransaction
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insertIgnoreAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

interface TechnologyRepository {

    suspend fun insertIfNotExists(technology: TechnologyRequest): Result<Int?>
    suspend fun selectById(id: Int): Result<ResultRow?>
    suspend fun selectByName(name: String): Result<ResultRow?>
    suspend fun selectAll(): Result<List<ResultRow>>
}

class TechnologyRepositoryImpl(private val dbTransaction: DbTransaction) : TechnologyRepository {

    override suspend fun insertIfNotExists(technology: TechnologyRequest): Result<Int?> {
        return dbTransaction.dbQuery {
            resultOf {
                Technologies.select(Technologies.value eq technology.value)
                    .map { it[Technologies.id] }
                    .firstOrNull()?.value
                    ?: Technologies.insertIgnoreAndGetId {
                        it[value] = technology.value
                    }?.value
            }
        }
    }

    override suspend fun selectById(id: Int): Result<ResultRow?> {
        return dbTransaction.dbQuery {
            resultOf {
                Technologies.select(Technologies.id eq id).firstOrNull()
            }
        }
    }

    override suspend fun selectByName(name: String): Result<ResultRow?> {
        return dbTransaction.dbQuery {
            resultOf {
                Technologies.select(Technologies.value eq name).firstOrNull()
            }
        }
    }

    override suspend fun selectAll(): Result<List<ResultRow>> {
        return dbTransaction.dbQuery {
            resultOf {
                Technologies.selectAll().toList()
            }
        }
    }
}