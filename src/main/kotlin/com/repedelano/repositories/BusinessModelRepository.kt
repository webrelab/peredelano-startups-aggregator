package com.repedelano.repositories

import com.repedelano.dtos.businessmodel.BusinessModelRequest
import com.repedelano.orm.businessmodel.BusinessModels
import com.repedelano.resultOf
import com.repedelano.utils.db.DbTransaction
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insertIgnoreAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

interface BusinessModelRepository {

    suspend fun add(businessModel: BusinessModelRequest): Result<Int?>
    suspend fun selectById(id: Int): Result<ResultRow?>
    suspend fun selectByName(name: String): Result<ResultRow?>
    suspend fun selectAll(): Result<List<ResultRow>>
    suspend fun update(id: Int, businessModel: BusinessModelRequest): Result<Boolean>
}

class BusinessModelRepositoryImpl(private val dbTransaction: DbTransaction) : BusinessModelRepository {

    override suspend fun add(businessModel: BusinessModelRequest): Result<Int?> {
        return dbTransaction.dbQuery {
            resultOf {
                BusinessModels.insertIgnoreAndGetId {
                    it[value] = businessModel.value
                    it[description] = businessModel.description
                }?.value
            }
        }
    }

    override suspend fun selectById(id: Int): Result<ResultRow?> {
        return dbTransaction.dbQuery {
            resultOf {
                BusinessModels.select(BusinessModels.id eq id).firstOrNull()
            }
        }
    }

    override suspend fun selectByName(name: String): Result<ResultRow?> {
        return dbTransaction.dbQuery {
            resultOf {
                BusinessModels.select(BusinessModels.value eq name).firstOrNull()
            }
        }
    }

    override suspend fun selectAll(): Result<List<ResultRow>> {
        return dbTransaction.dbQuery {
            resultOf {
                BusinessModels.selectAll()
                    .toList()
            }
        }
    }

    override suspend fun update(
        id: Int,
        businessModel: BusinessModelRequest
    ): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                BusinessModels.update( {BusinessModels.id eq id} ) {
                    it[value] = businessModel.value
                    it[description] = businessModel.description
                } > 0
            }
        }
    }
}