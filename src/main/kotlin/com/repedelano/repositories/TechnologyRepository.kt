package com.repedelano.repositories

import com.repedelano.dtos.technology.TechnologyRequest
import com.repedelano.orm.technology.Technologies
import com.repedelano.resultOf
import com.repedelano.utils.db.DbTransaction
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

interface TechnologyRepository {

    suspend fun insertIfNotExists(technology: TechnologyRequest): Result<Int?>
    suspend fun selectById(id: Int): Result<ResultRow?>
    suspend fun search(query: String): Result<List<ResultRow>>
    suspend fun selectAll(): Result<List<ResultRow>>
    suspend fun update(id: Int, technology: TechnologyRequest): Result<Boolean>
}

class TechnologyRepositoryImpl(private val dbTransaction: DbTransaction) : TechnologyRepository {

    override suspend fun insertIfNotExists(technology: TechnologyRequest): Result<Int?> {
        return dbTransaction.dbQuery {
            resultOf {
                Technologies.select(Technologies.value eq technology.value)
                    .map { it[Technologies.id] }
                    .firstOrNull()?.value
                    ?: Technologies.insertAndGetId {
                        it[value] = technology.value
                    }.value
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

    override suspend fun search(query: String): Result<List<ResultRow>> {
        return dbTransaction.dbQuery {
            resultOf {
                Technologies.select(Technologies.value.lowerCase() like "%${query.lowercase()}%").toList()
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

    override suspend fun update(id: Int, technology: TechnologyRequest): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                Technologies.update({ Technologies.id eq id }) {
                    it[value] = technology.value
                } > 0
            }
        }
    }
}