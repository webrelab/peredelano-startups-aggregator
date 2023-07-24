package com.repedelano.repositories

import com.repedelano.dtos.projectroles.ProjectRoleRequest
import com.repedelano.orm.projrctroles.ProjectRoles
import com.repedelano.resultOf
import com.repedelano.utils.db.DbTransaction
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

interface ProjectRoleRepository {

    suspend fun insertIfNotExists(projectRole: ProjectRoleRequest): Result<Int?>
    suspend fun selectById(id: Int): Result<ResultRow?>
    suspend fun search(query: String): Result<List<ResultRow>>
    suspend fun selectAll(): Result<List<ResultRow>>
    suspend fun update(id: Int, projectRole: ProjectRoleRequest): Result<Boolean>
}

class ProjectRoleRepositoryImpl(private val dbTransaction: DbTransaction) : ProjectRoleRepository {

    override suspend fun insertIfNotExists(projectRole: ProjectRoleRequest): Result<Int?> {
        return dbTransaction.dbQuery {
            resultOf {
                ProjectRoles.select(ProjectRoles.name eq projectRole.name)
                    .map { it[ProjectRoles.id].value }.firstOrNull()
                    ?: ProjectRoles.insertAndGetId {
                        it[name] = projectRole.name
                        it[description] = projectRole.description
                    }.value
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

    override suspend fun search(query: String): Result<List<ResultRow>> {
        return dbTransaction.dbQuery {
            resultOf {
                val lowerCaseQuery = query.lowercase()
                ProjectRoles.select(
                    ProjectRoles.name.lowerCase() like "%$lowerCaseQuery%" or
                        (ProjectRoles.description.lowerCase() like "%$lowerCaseQuery%")
                ).toList()
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

    override suspend fun update(id: Int, projectRole: ProjectRoleRequest): Result<Boolean> {
        return dbTransaction.dbQuery {
            resultOf {
                ProjectRoles.update({ ProjectRoles.id eq id }) {
                    it[name] = projectRole.name
                    it[description] = projectRole.description
                } > 0
            }
        }
    }
}