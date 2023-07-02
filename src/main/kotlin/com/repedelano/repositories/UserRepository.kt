package com.repedelano.repositories

import com.repedelano.dtos.Pager
import com.repedelano.dtos.user.UserRequest
import com.repedelano.orm.user.Users
import com.repedelano.resultOf
import com.repedelano.utils.db.DbTransaction
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insertIgnoreAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant

interface UserRepository {

    suspend fun insert(user: UserRequest): Result<Int?>
    suspend fun selectById(id: Int): Result<ResultRow?>
    suspend fun selectByPassportId(passportId: String): Result<ResultRow?>
    suspend fun selectByEmail(email: String): Result<ResultRow?>
    suspend fun selectAll(): Result<List<ResultRow>>
    suspend fun update(id: Int, user: UserRequest): Result<Boolean>
}

class UserRepositoryImpl(private val dbTransaction: DbTransaction) : UserRepository {

    override suspend fun insert(user: UserRequest): Result<Int?> {
        return dbTransaction.dbQuery {
            resultOf {
                val timestamp = Instant.now()
                Users.insertIgnoreAndGetId {
                    it[passportId] = user.passportId
                    it[email] = user.email
                    it[name] = user.name
                    it[lastName] = user.lastName
                    it[tgUser] = user.tgUser
                    it[picture] = user.picture
                    it[registered] = timestamp
                }?.value
            }
        }
    }

    override suspend fun selectById(id: Int): Result<ResultRow?> {
        return dbTransaction.dbQuery {
            resultOf {
                Users.select(Users.id eq id).firstOrNull()
            }
        }
    }

    override suspend fun selectByPassportId(passportId: String): Result<ResultRow?> {
        return dbTransaction.dbQuery {
            resultOf {
                Users.select(Users.passportId eq passportId) .firstOrNull()
            }
        }
    }

    override suspend fun selectByEmail(email: String): Result<ResultRow?> {
        return dbTransaction.dbQuery {
            resultOf {
                Users.select(Users.email eq email).firstOrNull()
            }
        }
    }

    override suspend fun selectAll(): Result<List<ResultRow>> {
        return dbTransaction.dbQuery {
            resultOf {
                Users.selectAll().toList()
            }
        }
    }

    override suspend fun update(id: Int, user: UserRequest): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                Users.update({ Users.id eq id }) {
                    it[passportId] = user.passportId
                    it[email] = user.email
                    it[name] = user.name
                    it[lastName] = user.lastName
                    it[tgUser] = user.tgUser
                    it[picture] = user.picture
                } > 0
            }
        }
    }
}