package com.repedelano.routes

import com.repedelano.deconstructResult
import com.repedelano.dtos.Pager
import com.repedelano.dtos.SerializedException
import com.repedelano.dtos.idea.IdeaRequest
import com.repedelano.extensions.toIdeaStage
import com.repedelano.extensions.toUuidOrNull
import com.repedelano.routes.IdeaRoutes.Companion.ADD_IDEA
import com.repedelano.routes.IdeaRoutes.Companion.IDEAS
import com.repedelano.routes.IdeaRoutes.Companion.STAGE
import com.repedelano.routes.IdeaRoutes.Companion.serverIdeaWithId
import com.repedelano.routes.IdeaRoutes.Companion.serverRequestStage
import com.repedelano.routes.RouteConstants.Companion.API_V1
import com.repedelano.routes.RouteConstants.Companion.ID
import com.repedelano.usecases.AddIdeaUseCase
import com.repedelano.usecases.GetIdeaUseCase
import com.repedelano.usecases.GetIdeasUseCase
import com.repedelano.usecases.UpdateIdeaStageUseCase
import com.repedelano.usecases.UpdateIdeaUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import org.koin.ktor.ext.inject
import java.util.UUID

class IdeaRoutes {

    companion object {
        const val IDEA = "$API_V1/idea"
        const val ADD_IDEA = "$IDEA/add"
        const val IDEAS = "$API_V1/ideas"
        const val STAGE: String = "stage"

        fun serverIdeaWithId() = "$IDEA/{$ID}"

        fun clientIdeaWithId(id: String?) = id?.let { "$IDEA/$it" } ?: IDEA

        fun clientGetPage(page: String?, itemsPerPage: String?) = PagerRoutes.clientAddPager(IDEAS, page, itemsPerPage)

        fun serverRequestStage() = "$IDEA/{$ID}/{$STAGE}"

        fun clientRequestStage(id: Any?, stage: Any?) = listOfNotNull(
            id?.let { "$ID=$it" },
            stage?.let { "$STAGE=$it" }
        ).joinToString("&").let {
            if (it.isBlank()) IDEA
            else "$IDEA?$it"
        }
    }
}

fun Routing.ideaRoutes() {
    addIdea()
    getIdea()
    getIdeas()
    updateIdea()
    updateIdeaStage()
}

private fun Routing.addIdea() {
    val addIdeaUseCase by inject<AddIdeaUseCase>()
    post(ADD_IDEA) {
        try {
            call.receiveNullable<IdeaRequest>()
                ?.let { idea ->
                    val result = addIdeaUseCase.addIdea(idea)
                    deconstructResult(this, result, HttpStatusCode.Created)
                }
                ?: call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid or missing IdeaRequest"
                )
        } catch (e: Throwable) {
            call.respond(
                HttpStatusCode.InternalServerError,
                SerializedException(e.message)
            )
        }
    }
}

private fun Routing.getIdea() {
    val getIdeaUseCase by inject<GetIdeaUseCase>()
    get(serverIdeaWithId()) {
        try {
            val id = call.parameters[ID]
            id?.let {
                val result = getIdeaUseCase.getIdea(UUID.fromString(id))
                deconstructResult(this, result, HttpStatusCode.OK)
            }
            call.respond(HttpStatusCode.NotFound)
        } catch (e: Throwable) {
            call.respond(
                HttpStatusCode.InternalServerError,
                SerializedException(e.message)
            )
        }
    }
}

private fun Routing.getIdeas() {
    val getIdeasUseCase by inject<GetIdeasUseCase>()
    get(IDEAS) {
        try {
            val page = call.parameters[PagerRoutes.PAGE]?.toIntOrNull() ?: 0
            val itemsPerPage = call.parameters[PagerRoutes.ITEMS_PER_PAGE]?.toIntOrNull() ?: 20
            val pager = Pager(page, itemsPerPage)
            val result = getIdeasUseCase.getPage(pager)
            deconstructResult(this, result, HttpStatusCode.OK)
        } catch (e: Throwable) {
            call.respond(
                HttpStatusCode.InternalServerError,
                SerializedException(e.message)
            )
        }
    }
}

private fun Routing.updateIdea() {
    val updateIdeaUseCase by inject<UpdateIdeaUseCase>()
    put(serverIdeaWithId()) {
        try {
            call.parameters[ID]?.toUuidOrNull()
                ?.let { id ->
                    call.receiveNullable<IdeaRequest>()
                        ?.let { idea ->
                            val result = updateIdeaUseCase.update(id, idea)
                            deconstructResult(this, result, HttpStatusCode.OK)
                        }
                        ?: call.respond(
                            HttpStatusCode.BadRequest,
                            "Invalid or missing IdeaRequest"
                        )
                }
                ?: call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid or missing UUID"
                )
        } catch (e: Throwable) {
            call.respond(
                HttpStatusCode.InternalServerError,
                SerializedException(e.message)
            )
        }
    }
}

private fun Routing.updateIdeaStage() {
    val updateIdeaStageUseCase by inject<UpdateIdeaStageUseCase>()
    put(serverRequestStage()) {
        try {
            call.parameters[ID]?.toUuidOrNull()
                ?.let { id ->
                    call.parameters[STAGE]?.toIdeaStage()
                        ?.let { stage ->
                            val result = updateIdeaStageUseCase.update(id, stage)
                            deconstructResult(this, result, HttpStatusCode.OK)
                        }
                        ?: call.respond(
                            HttpStatusCode.BadRequest,
                            "Invalid or missing IdeaStage"
                        )
                }
                ?: call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid or missing UUID"
                )
        } catch (e: Throwable) {
            call.respond(
                HttpStatusCode.InternalServerError,
                SerializedException(e.message)
            )
        }
    }
}