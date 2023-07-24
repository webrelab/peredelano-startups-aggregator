package com.repedelano.routes

import com.repedelano.deconstructResult
import com.repedelano.dtos.SerializedException
import com.repedelano.dtos.technology.TechnologyRequest
import com.repedelano.routes.RouteConstants.Companion.API_V1
import com.repedelano.routes.RouteConstants.Companion.ID
import com.repedelano.routes.RouteConstants.Companion.QUERY
import com.repedelano.routes.TechnologyRoutes.Companion.ADD_TECH
import com.repedelano.routes.TechnologyRoutes.Companion.TECHS
import com.repedelano.routes.TechnologyRoutes.Companion.serverTechnologyWithId
import com.repedelano.usecases.AddTechnologyUseCase
import com.repedelano.usecases.GetAllTechnologiesUseCase
import com.repedelano.usecases.GetTechnologyByIdUseCase
import com.repedelano.usecases.SearchTechnologyUseCase
import com.repedelano.usecases.UpdateTechnologyUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import org.koin.ktor.ext.inject

class TechnologyRoutes {

    companion object {

        const val TECH = "$API_V1/technology"
        const val TECHS = "$API_V1/technologies"
        const val ADD_TECH = "$TECH/add"

        fun serverTechnologyWithId() = "$TECH/{$ID}"
        fun clientTechnologyWithId(id: Any? = null) = id?.let { "$TECH/$id" } ?: TECH
        fun clientSearchTechnology(query: Any? = null) = query?.let { "$TECHS?$QUERY=$query" } ?: TECHS

    }
}

fun Routing.technologyRoutes() {
    addTechnology()
    getTechnologyById()
    searchTechnology()
    updateTechnology()
}

private fun Routing.addTechnology() {
    val addTechnologyUseCase by inject<AddTechnologyUseCase>()
    post(ADD_TECH) {
        try {
            call.receiveNullable<TechnologyRequest>()
                ?.let {
                    val result = addTechnologyUseCase.add(it)
                    deconstructResult(this, result, HttpStatusCode.Created)
                }
                ?: call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid or missing TechnologyRequest"
                )
        } catch (e: Throwable) {
            call.respond(
                HttpStatusCode.InternalServerError,
                SerializedException(e.message)
            )
        }
    }
}

private fun Routing.getTechnologyById() {
    val getTechnologyByIdUseCase by inject<GetTechnologyByIdUseCase>()
    get(serverTechnologyWithId()) {
        try {
            call.parameters[ID]?.toIntOrNull()
                ?.let {
                    val result = getTechnologyByIdUseCase.get(it)
                    deconstructResult(this, result, HttpStatusCode.OK)
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

private fun Routing.searchTechnology() {
    val searchTechnologyUseCase by inject<SearchTechnologyUseCase>()
    val getAllTechnologiesUseCase by inject<GetAllTechnologiesUseCase>()
    get(TECHS) {
        try {
            val result = call.parameters[QUERY]?.let {
                searchTechnologyUseCase.search(it)
            } ?: getAllTechnologiesUseCase.getAll()
            deconstructResult(this, result, HttpStatusCode.OK)
        } catch (e: Throwable) {
            call.respond(
                HttpStatusCode.InternalServerError,
                SerializedException(e.message)
            )
        }
    }
}

fun Routing.updateTechnology() {
    val updateTechnologyUseCase by inject<UpdateTechnologyUseCase>()
    put(serverTechnologyWithId()) {
        try {
            call.parameters[ID]?.toIntOrNull()
                ?.let { id ->
                    call.receiveNullable<TechnologyRequest>()
                        ?.let { technologyRequest ->
                            val result = updateTechnologyUseCase.update(id, technologyRequest)
                            deconstructResult(this, result, HttpStatusCode.OK)
                        }
                        ?: call.respond(
                            HttpStatusCode.BadRequest,
                            "Invalid or missing TechnologyRequest"
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