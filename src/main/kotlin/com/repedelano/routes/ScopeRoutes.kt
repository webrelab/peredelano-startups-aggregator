package com.repedelano.routes

import com.repedelano.deconstructResult
import com.repedelano.dtos.SerializedException
import com.repedelano.dtos.scope.ScopeRequest
import com.repedelano.routes.RouteConstants.Companion.API_V1
import com.repedelano.routes.RouteConstants.Companion.ID
import com.repedelano.routes.RouteConstants.Companion.QUERY
import com.repedelano.routes.ScopeRoutes.Companion.ADD_SCOPE
import com.repedelano.routes.ScopeRoutes.Companion.SCOPES
import com.repedelano.routes.ScopeRoutes.Companion.serverScopeWithId
import com.repedelano.usecases.AddScopeUseCase
import com.repedelano.usecases.GetScopeByIdUseCase
import com.repedelano.usecases.GetScopesUseCase
import com.repedelano.usecases.SearchScopeUseCase
import com.repedelano.usecases.UpdateScopeUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import org.koin.ktor.ext.inject

class ScopeRoutes {

    companion object {

        const val SCOPE = "$API_V1/scope"
        const val SCOPES = "$API_V1/scopes"
        const val ADD_SCOPE = "$SCOPE/add"

        fun serverScopeWithId() = "$SCOPE/{$ID}"

        fun clientScopeWithId(id: Any?) = id?.let { "$SCOPE/$it" } ?: SCOPE
    }
}

fun Routing.scopeRoutes() {
    addScope()
    getScopeById()
    searchScope()
    updateScope()
}

private fun Routing.addScope() {
    val addScopeUseCase by inject<AddScopeUseCase>()
    post(ADD_SCOPE) {
        try {
            call.receiveNullable<ScopeRequest>()
                ?.let {
                    val result = addScopeUseCase.add(it)
                    deconstructResult(this, result, HttpStatusCode.OK)
                }
                ?: call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid or missing ScopeRequest"
                )
        } catch (e: Throwable) {
            call.respond(
                HttpStatusCode.InternalServerError,
                SerializedException(e.message)
            )
        }
    }
}

private fun Routing.getScopeById() {
    val getScopeByIdUseCase by inject<GetScopeByIdUseCase>()
    get(serverScopeWithId()) {
        try {
            call.parameters[ID]?.toIntOrNull()
                ?.let {
                    val result = getScopeByIdUseCase.get(it)
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

private fun Routing.searchScope() {
    val searchScopeUseCase by inject<SearchScopeUseCase>()
    val getScopesUseCase by inject<GetScopesUseCase>()
    get(SCOPES) {
        try {
            val result = call.parameters[QUERY]
                ?.let { query ->
                    searchScopeUseCase.search(query)
                }
                ?: getScopesUseCase.getAll()
            deconstructResult(this, result, HttpStatusCode.OK)
        } catch (e: Throwable) {
            call.respond(
                HttpStatusCode.InternalServerError,
                SerializedException(e.message)
            )
        }
    }
}

private fun Routing.updateScope() {
    val updateScopeUseCase by inject<UpdateScopeUseCase>()
    put(serverScopeWithId()) {
        try {
            call.parameters[ID]?.toIntOrNull()
                ?.let { id ->
                    call.receiveNullable<ScopeRequest>()
                        ?.let { scope ->
                            val result = updateScopeUseCase.update(id, scope)
                            deconstructResult(this, result, HttpStatusCode.OK)
                        }
                        ?: call.respond(
                            HttpStatusCode.BadRequest,
                            "Invalid or missing ScopeRequest"
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