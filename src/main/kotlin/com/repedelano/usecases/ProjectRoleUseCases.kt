package com.repedelano.usecases

import com.repedelano.dtos.projectroles.ProjectRoleRequest
import com.repedelano.dtos.projectroles.ProjectRoleResponse
import com.repedelano.dtos.projectroles.ProjectRoleResponseList
import com.repedelano.services.ProjectRoleService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

fun interface AddProjectRoleUseCase {

    suspend fun add(projectRole: ProjectRoleRequest): Result<ProjectRoleResponse?>
}

fun interface GetProjectRoleByIdUseCase {

    suspend fun get(id: Int): Result<ProjectRoleResponse?>
}

fun interface SearchProjectRoleUseCase {

    suspend fun search(query: String): Result<ProjectRoleResponseList>
}

fun interface GetProjectRolesUseCase {

    suspend fun getAll(): Result<ProjectRoleResponseList>
}

fun interface UpdateProjectRoleUseCase {

    suspend fun update(id: Int, projectRole: ProjectRoleRequest): Result<ProjectRoleResponse?>
}

fun addProjectRoleUseCase(
    dispatcher: CoroutineDispatcher,
    projectRoleService: ProjectRoleService
) = AddProjectRoleUseCase { projectRole ->
    withContext(dispatcher) {
        projectRoleService.add(projectRole)
    }
}

fun getProjectRoleByIdUseCase(
    dispatcher: CoroutineDispatcher,
    projectRoleService: ProjectRoleService
) = GetProjectRoleByIdUseCase { id ->
    withContext(dispatcher) {
        projectRoleService.selectById(id)
    }
}

fun searchProjectRoleUseCase(
    dispatcher: CoroutineDispatcher,
    projectRoleService: ProjectRoleService
) = SearchProjectRoleUseCase { query ->
    withContext(dispatcher) {
        projectRoleService.search(query)
    }
}

fun getProjectRolesUseCase(
    dispatcher: CoroutineDispatcher,
    projectRoleService: ProjectRoleService
) = GetProjectRolesUseCase {
    withContext(dispatcher) {
        projectRoleService.selectAll()
    }
}

fun updateProjectRoleUseCase(
    dispatcher: CoroutineDispatcher,
    projectRoleService: ProjectRoleService
) = UpdateProjectRoleUseCase {id, projectRole ->
    withContext(dispatcher) {
        projectRoleService.update(id, projectRole)
    }
}