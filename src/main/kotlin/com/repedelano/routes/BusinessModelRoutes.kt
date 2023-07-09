package com.repedelano.routes

import com.repedelano.deconstructResult
import com.repedelano.dtos.SerializedException
import com.repedelano.dtos.businessmodel.BusinessModelRequest
import com.repedelano.routes.BusinessModelRoutes.Companion.ADD_BM
import com.repedelano.routes.BusinessModelRoutes.Companion.BMS
import com.repedelano.routes.BusinessModelRoutes.Companion.serverBmWithId
import com.repedelano.routes.RouteConstants.Companion.API_V1
import com.repedelano.routes.RouteConstants.Companion.ID
import com.repedelano.usecases.AddBusinessModelUseCase
import com.repedelano.usecases.GetBusinessModelByIdUseCase
import com.repedelano.usecases.GetBusinessModelsUseCase
import com.repedelano.usecases.UpdateBusinessModelUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import org.koin.ktor.ext.inject

class BusinessModelRoutes {

    companion object {
        const val BM = "$API_V1/business-model"
        const val BMS = "$API_V1/business-models"
        const val ADD_BM = "$BM/add"

        fun serverBmWithId() = "$BM/{$ID}"

        fun clientBmWithId(id: Any?) = id?.let { "$BM/$it" } ?: BM
    }
}

fun Routing.businessModelRoutes() {
    addBusinessModel()
    getBusinessModelById()
    getBusinessModels()
    updateBusinessModel()
}

private fun Routing.addBusinessModel() {
    val addBusinessModelUseCase by inject<AddBusinessModelUseCase>()
    post(ADD_BM) {
        try {
            call.receiveNullable<BusinessModelRequest>()
                ?.let {
                    val result = addBusinessModelUseCase.add(it)
                    deconstructResult(this, result, HttpStatusCode.Created)
                }
                ?: call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid or missing BusinessModelRequest"
                )
        } catch (e: Throwable) {
            call.respond(
                HttpStatusCode.InternalServerError,
                SerializedException(e.message)
            )
        }
    }
}

private fun Routing.getBusinessModelById() {
    val getBusinessModelByIdUseCase by inject<GetBusinessModelByIdUseCase>()
    get(serverBmWithId()) {
        try {
            call.parameters[ID]?.toIntOrNull()
                ?.let {
                    val result = getBusinessModelByIdUseCase.get(it)
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

private fun Routing.getBusinessModels() {
    val getBusinessModelsUseCase by inject<GetBusinessModelsUseCase>()
    get(BMS) {
        try {
            val result = getBusinessModelsUseCase.getAll()
            deconstructResult(this, result, HttpStatusCode.OK)
        } catch (e: Throwable) {
            call.respond(
                HttpStatusCode.InternalServerError,
                SerializedException(e.message)
            )
        }
    }
}

private fun Routing.updateBusinessModel() {
    val updateBusinessModelUseCase by inject<UpdateBusinessModelUseCase>()
    put(serverBmWithId()) {
        try {
            call.parameters[ID]?.toIntOrNull()
                ?.let { id ->
                    call.receiveNullable<BusinessModelRequest>()
                        ?.let { model ->
                            val result = updateBusinessModelUseCase.update(id, model)
                            deconstructResult(this, result, HttpStatusCode.OK)
                        }
                        ?: call.respond(
                            HttpStatusCode.BadRequest,
                            "Invalid or missing BusinessModelRequest"
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