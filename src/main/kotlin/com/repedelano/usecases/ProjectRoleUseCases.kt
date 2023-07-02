package com.repedelano.usecases

import com.repedelano.dtos.projectroles.ProjectRoleResponse
import com.repedelano.services.ProjectRoleService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

fun interface GetProjectRoleByIdUseCase {

    suspend fun get(id: Int): Result<ProjectRoleResponse?>
}

fun getProjectRoleByIdUseCase(
    dispatcher: CoroutineDispatcher,
    projectRoleService: ProjectRoleService
) = GetProjectRoleByIdUseCase { id ->
    withContext(dispatcher) {
        projectRoleService.selectById(id)
    }
}