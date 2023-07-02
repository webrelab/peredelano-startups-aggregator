package com.repedelano.repositories

import com.repedelano.dtos.projectroles.ProjectRoleRequest
import com.repedelano.orm.projrctroles.ProjectRoles
import com.repedelano.resultOf
import com.repedelano.utils.db.DbTransaction
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insertIgnoreAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

interface ProjectRoleRepository {

    suspend fun insert(projectRole: ProjectRoleRequest): Result<Int?>
    suspend fun selectById(id: Int): Result<ResultRow?>
    suspend fun selectByName(name: String): Result<ResultRow?>
    suspend fun selectAll(): Result<List<ResultRow>>
}

class ProjectRoleRepositoryImpl(private val dbTransaction: DbTransaction) : ProjectRoleRepository {

    override suspend fun insert(projectRole: ProjectRoleRequest): Result<Int?> {
        return dbTransaction.dbQuery {
            resultOf {
                ProjectRoles.insertIgnoreAndGetId {
                    it[name] = projectRole.name
                    it[description] = projectRole.description
                }?.value
            }
        }
    }

    override suspend fun selectById(id: Int): Result<ResultRow?> {
        return dbTransaction.dbQuery {
            resultOf {
                ProjectRoles.select(ProjectRoles.id eq id).firstOrNull()
            }
        }
    }

    override suspend fun selectByName(name: String): Result<ResultRow?> {
        return dbTransaction.dbQuery {
            resultOf {
                ProjectRoles.select(ProjectRoles.name eq name).firstOrNull()
            }
        }
    }

    override suspend fun selectAll(): Result<List<ResultRow>> {
        return dbTransaction.dbQuery {
            resultOf {
                ProjectRoles.selectAll().toList()
            }
        }
    }
}