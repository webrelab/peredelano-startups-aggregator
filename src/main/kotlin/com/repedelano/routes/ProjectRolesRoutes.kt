package com.repedelano.routes

import com.repedelano.deconstructResult
import com.repedelano.dtos.SerializedException
import com.repedelano.dtos.projectroles.ProjectRoleRequest
import com.repedelano.routes.ProjectRolesRoutes.Companion.ADD_PR
import com.repedelano.routes.ProjectRolesRoutes.Companion.PRS
import com.repedelano.routes.ProjectRolesRoutes.Companion.serverPrWithId
import com.repedelano.routes.RouteConstants.Companion.API_V1
import com.repedelano.routes.RouteConstants.Companion.ID
import com.repedelano.routes.RouteConstants.Companion.QUERY
import com.repedelano.usecases.AddProjectRoleUseCase
import com.repedelano.usecases.GetProjectRoleByIdUseCase
import com.repedelano.usecases.GetProjectRolesUseCase
import com.repedelano.usecases.SearchProjectRoleUseCase
import com.repedelano.usecases.UpdateProjectRoleUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import org.koin.ktor.ext.inject

class ProjectRolesRoutes {

    companion object {
        const val PR = "$API_V1/project-role"
        const val ADD_PR = "$PR/add"
        const val PRS = "$API_V1/project-roles"

        fun serverPrWithId() = "$PR/{$ID}"

        fun clientPrWithId(id: Any?) = id?.let { "$PR/$id" } ?: PR

        fun clientPrsSearch(query: Any?) = query?.let { "$PRS?$QUERY=$query" } ?: PRS
    }
}

fun Routing.projectRolesRoutes() {
    addProjectRole()
    getProjectRoleById()
    searchProjectRoles()
    updateProjectRole()
}

private fun Routing.addProjectRole() {
    val addProjectRoleUseCase by inject<AddProjectRoleUseCase>()
    post(ADD_PR) {
        try {
            call.receiveNullable<ProjectRoleRequest>()
                ?.let {
                    val result = addProjectRoleUseCase.add(it)
                    deconstructResult(this, result, HttpStatusCode.Created)
                }
                ?: call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid or missing ProjectRoleRequest"
                )
        } catch (e: Throwable) {
            call.respond(
                HttpStatusCode.InternalServerError,
                SerializedException(e.message)
            )
        }
    }
}

private fun Routing.getProjectRoleById() {
    val getProjectRoleByIdUseCase by inject<GetProjectRoleByIdUseCase>()
    get(serverPrWithId()) {
        try {
            call.parameters[ID]?.toIntOrNull()
                ?.let { id ->
                    val result = getProjectRoleByIdUseCase.get(id)
                    deconstructResult(this, result, HttpStatusCode.OK)
                }
                ?: call.respond(HttpStatusCode.NotFound)
        } catch (e: Throwable) {
            call.respond(
                HttpStatusCode.InternalServerError,
                SerializedException(e.message)
            )
        }
    }
}

private fun Routing.searchProjectRoles() {
    val searchProjectRoleUseCase by inject<SearchProjectRoleUseCase>()
    val getProjectRolesUseCase by inject<GetProjectRolesUseCase>()
    get(PRS) {
        try {
            val result = call.parameters[QUERY]?.let {
                searchProjectRoleUseCase.search(it)
            } ?: getProjectRolesUseCase.getAll()
            deconstructResult(this, result, HttpStatusCode.OK)
        } catch (e: Throwable) {
            call.respond(
                HttpStatusCode.InternalServerError,
                SerializedException(e.message)
            )
        }
    }
}

private fun Routing.updateProjectRole() {
    val updateProjectRoleUseCase by inject<UpdateProjectRoleUseCase>()
    put(serverPrWithId()) {
        try {
            call.parameters[ID]?.toIntOrNull()
                ?.let { id ->
                    call.receiveNullable<ProjectRoleRequest>()
                        ?.let { projectRoleRequest ->
                            val result = updateProjectRoleUseCase.update(id, projectRoleRequest)
                            deconstructResult(this, result, HttpStatusCode.OK)
                        }
                        ?: call.respond(
                            HttpStatusCode.BadRequest,
                            "Invalid or missing ProjectRoleRequest"
                        )
                }
                ?: call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid or missing ID"
                )
        } catch (e: Throwable) {
            call.respond(
                HttpStatusCode.InternalServerError,
                SerializedException(e.message)
            )
        }
    }
}