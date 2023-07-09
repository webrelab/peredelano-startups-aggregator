package com.repedelano.services

import com.repedelano.dtos.projectroles.ProjectRoleRequest
import com.repedelano.dtos.projectroles.ProjectRoleResponse
import com.repedelano.dtos.projectroles.ProjectRoleResponseList
import com.repedelano.flatMap
import com.repedelano.orm.helpers.toProjectRoleResponse
import com.repedelano.orm.helpers.toProjectRoleResponseList
import com.repedelano.repositories.ProjectRoleRepository

interface ProjectRoleService {

    suspend fun add(projectRole: ProjectRoleRequest): Result<ProjectRoleResponse?>
    suspend fun selectById(id: Int): Result<ProjectRoleResponse?>
    suspend fun search(query: String): Result<ProjectRoleResponseList>
    suspend fun selectAll(): Result<ProjectRoleResponseList>
    suspend fun update(id: Int, projectRole: ProjectRoleRequest): Result<ProjectRoleResponse?>
}

class ProjectRoleServiceImpl(private val projectRoleRepository: ProjectRoleRepository) : ProjectRoleService {

    override suspend fun add(projectRole: ProjectRoleRequest) =
        projectRoleRepository.insert(projectRole).flatMap { selectById(it!!) }

    override suspend fun selectById(id: Int) =
        projectRoleRepository.selectById(id).map { it!!.toProjectRoleResponse() }

    override suspend fun search(query: String) =
        projectRoleRepository.search(query).map { list ->
            list.map { it.toProjectRoleResponse() }.toProjectRoleResponseList()
        }

    override suspend fun selectAll() =
        projectRoleRepository.selectAll().map { list ->
            list.map { it.toProjectRoleResponse() }.toProjectRoleResponseList()
        }

    override suspend fun update(id: Int, projectRole: ProjectRoleRequest) =
        projectRoleRepository.update(id, projectRole)
            .flatMap { selectById(id) }
}