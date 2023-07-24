package com.repedelano.routes

import com.repedelano.deconstructResult
import com.repedelano.dtos.Pager
import com.repedelano.dtos.SerializedException
import com.repedelano.dtos.idea.IdeaRequest
import com.repedelano.dtos.idea.IdeaSearchRequest
import com.repedelano.dtos.idea.IdeaStageRequest
import com.repedelano.extensions.isEmpty
import com.repedelano.extensions.toUuidOrNull
import com.repedelano.routes.IdeaRoutes.Companion.ADD_IDEA
import com.repedelano.routes.IdeaRoutes.Companion.BM
import com.repedelano.routes.IdeaRoutes.Companion.IDEAS
import com.repedelano.routes.IdeaRoutes.Companion.OWNER
import com.repedelano.routes.IdeaRoutes.Companion.QUERY_STRING
import com.repedelano.routes.IdeaRoutes.Companion.SCOPES
import com.repedelano.routes.IdeaRoutes.Companion.TS
import com.repedelano.routes.IdeaRoutes.Companion.serverIdeaWithId
import com.repedelano.routes.IdeaRoutes.Companion.serverRequestStage
import com.repedelano.routes.PagerRoutes.Companion.ITEMS_PER_PAGE
import com.repedelano.routes.PagerRoutes.Companion.PAGE
import com.repedelano.routes.PagerRoutes.Companion.clientAddPager
import com.repedelano.routes.RouteConstants.Companion.API_V1
import com.repedelano.routes.RouteConstants.Companion.ID
import com.repedelano.usecases.AddIdeaUseCase
import com.repedelano.usecases.GetIdeaUseCase
import com.repedelano.usecases.GetIdeasUseCase
import com.repedelano.usecases.SearchIdeaUseCase
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
        const val OWNER = "owner"
        const val QUERY_STRING = "queryString"
        const val SCOPES = "scopes"
        const val BM = "businessModels"
        const val TS = "techStack"

        fun serverIdeaWithId() = "$IDEA/{$ID}"

        fun serverRequestStage() = "$IDEA/$STAGE/{$ID}"

        fun clientIdeaWithId(id: Any?) = id?.let { "$IDEA/$id" } ?: IDEA

        fun clientUpdateStage(id: Any?) = id?.let { "$IDEA/$STAGE/$it" } ?: "$IDEA/$STAGE"

        fun clientGetPage(page: Any?, itemsPerPage: Any?) = PagerRoutes.clientAddPager(IDEAS, page, itemsPerPage)
        fun clientIdeaSearch(
            owner: Any? = null,
            queryString: Any? = null,
            scopes: List<Any>? = null,
            businessModels: List<Any>? = null,
            techStack: List<Any>? = null,
            page: Any? = null,
            itemsPerPage: Any? = null,
        ) = listOfNotNull(
            owner?.let {"$OWNER=$owner"},
            queryString?.let{"$QUERY_STRING=$queryString"},
            scopes?.let{"$SCOPES=${it.joinToString(",")}"},
            businessModels?.let{"$BM=${it.joinToString(",")}"},
            techStack?.let{"$TS=${it.joinToString(",")}"}
        ).joinToString("&")
            .let {
                if (it.isBlank()) IDEAS
                else "$IDEAS?$it"
            }.let { clientAddPager(it, page, itemsPerPage) }

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
    val searchIdeaUseCase by inject<SearchIdeaUseCase>()
    get(IDEAS) {
        try {
            val scopes = call.parameters[SCOPES]
                ?.split(",")
                ?.mapNotNull { it.trim().toIntOrNull() }
                ?: listOf()
            val businessModels = call.parameters[BM]
                ?.split(",")
                ?.mapNotNull { it.trim().toIntOrNull() }
                ?: listOf()
            val techStack = call.parameters[TS]
                ?.split(",")
                ?.mapNotNull { it.trim().toIntOrNull() }
                ?: listOf()
            val searchQuery = IdeaSearchRequest(
                owner = call.parameters[OWNER]?.toIntOrNull(),
                queryString = call.parameters[QUERY_STRING],
                scopes = scopes,
                businessModels = businessModels,
                techStack = techStack
            )
            val pager = Pager.of(
                call.parameters[PAGE]?.toIntOrNull(),
                call.parameters[ITEMS_PER_PAGE]?.toIntOrNull()
            )
            val result = when {
                searchQuery.isEmpty() -> getIdeasUseCase.getPage(pager)
                else -> searchIdeaUseCase.search(pager, searchQuery)
            }
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
                    call.receiveNullable<IdeaStageRequest>()
                        ?.let { stage ->
                            val result = updateIdeaStageUseCase.update(id, stage.stage)
                            deconstructResult(this, result, HttpStatusCode.OK)
                        }
                        ?: call.respond(
                            HttpStatusCode.BadRequest,
                            "Invalid or missing IdeaStageRequest"
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