package com.repedelano.routes

import com.repedelano.deconstructResult
import com.repedelano.dtos.Pager
import com.repedelano.dtos.SerializedException
import com.repedelano.dtos.idea.IdeaStatus
import com.repedelano.dtos.vacancy.VacancyRequest
import com.repedelano.dtos.vacancy.VacancyRequestStatus
import com.repedelano.dtos.vacancy.VacancySearchQuery
import com.repedelano.dtos.vacancy.VacancyStatus
import com.repedelano.extensions.toUuidOrNull
import com.repedelano.routes.PagerRoutes.Companion.ITEMS_PER_PAGE
import com.repedelano.routes.PagerRoutes.Companion.PAGE
import com.repedelano.routes.PagerRoutes.Companion.clientAddPager
import com.repedelano.routes.RouteConstants.Companion.API_V1
import com.repedelano.routes.RouteConstants.Companion.ID
import com.repedelano.routes.VacancyRoutes.Companion.ADD_VAC
import com.repedelano.routes.VacancyRoutes.Companion.DESCRIPTION
import com.repedelano.routes.VacancyRoutes.Companion.IDEA_ID
import com.repedelano.routes.VacancyRoutes.Companion.PROJECT_ROLE
import com.repedelano.routes.VacancyRoutes.Companion.STATUS
import com.repedelano.routes.VacancyRoutes.Companion.TECH_STACK
import com.repedelano.routes.VacancyRoutes.Companion.VACS
import com.repedelano.routes.VacancyRoutes.Companion.serverVacancyUpdateStatus
import com.repedelano.routes.VacancyRoutes.Companion.serverVacancyWithId
import com.repedelano.usecases.AddVacancyUseCase
import com.repedelano.usecases.GetVacancyByIdUseCase
import com.repedelano.usecases.SearchVacancyUseCase
import com.repedelano.usecases.UpdateVacancyStatusUseCase
import com.repedelano.usecases.UpdateVacancyUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import org.koin.ktor.ext.inject

class VacancyRoutes {

    companion object {

        const val VAC = "$API_V1/vacancy"
        const val VACS = "$API_V1/vacancies"
        const val ADD_VAC = "$VAC/add"
        const val IDEA_ID = "ideaId"
        const val PROJECT_ROLE = "projectRole"
        const val TECH_STACK = "techStack"
        const val DESCRIPTION = "description"
        const val STATUS = "status"

        fun serverVacancyWithId() = "$VAC/{$ID}"

        fun serverVacancyUpdateStatus() = "$VAC/$STATUS/{$ID}"

        fun clientVacancyWithId(id: Any? = null) = id?.let { "$VAC/$it" } ?: VAC

        fun clientVacancyUpdateStatus(id: Any? = null) = id?.let { "$VAC/$STATUS/$id"} ?: "$VAC/$STATUS"

        fun clientVacancySearch(
            ideaId: Any? = null,
            projectRole: Any? = null,
            techStack: List<Any> = listOf(),
            description: Any? = null,
            status: Any? = null,
            page: Any? = 0,
            itemsPerPage: Any? = 20,
        ) = listOfNotNull(
            ideaId?.let { "$IDEA_ID=$it" },
            projectRole?.let { "$PROJECT_ROLE=$it" },
            status?.let { "$STATUS=$status"},
            if (techStack.isNotEmpty()) "$TECH_STACK=${techStack.joinToString(",")}"
            else null,
            description?.let { "$DESCRIPTION=$it" }
        ).joinToString("&")
            .let {
                if (it.isBlank()) VACS
                else "$VACS?$it"
            }.let { clientAddPager(it, page, itemsPerPage) }
    }
}

fun Routing.vacancyRoutes() {
    addVacancy()
    getVacancyById()
    searchVacancy()
    updateVacancy()
    updateStatus()
}

private fun Routing.addVacancy() {
    val addVacancyUseCase by inject<AddVacancyUseCase>()
    post(ADD_VAC) {
        try {
            call.receiveNullable<VacancyRequest>()
                ?.let {
                    val result = addVacancyUseCase.add(it)
                    deconstructResult(this, result, HttpStatusCode.Created)
                }
                ?: call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid or missing VacancyRequest"
                )
        } catch (e: Throwable) {
            call.respond(
                HttpStatusCode.InternalServerError,
                SerializedException(e.message)
            )
        }
    }
}

private fun Routing.getVacancyById() {
    val getVacancyByIdUseCase by inject<GetVacancyByIdUseCase>()
    get(serverVacancyWithId()) {
        try {
            call.parameters[ID]?.toIntOrNull()
                ?.let {
                    val result = getVacancyByIdUseCase.getById(it)
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

private fun Routing.searchVacancy() {
    val searchVacancyUseCase by inject<SearchVacancyUseCase>()
    get(VACS) {
        try {
            val techStack = call.parameters[TECH_STACK]
                ?.split(",")
                ?.mapNotNull { it.trim().toIntOrNull() }
                ?: listOf()
            val searchQuery = VacancySearchQuery(
                ideaId = call.parameters[IDEA_ID]?.toUuidOrNull(),
                projectRole = call.parameters[PROJECT_ROLE]?.toIntOrNull(),
                techStack = techStack,
                description = call.parameters[DESCRIPTION],
                status = VacancyStatus.of(call.parameters[STATUS])
            )
            val pager = Pager.of(
                call.parameters[PAGE]?.toIntOrNull(),
                call.parameters[ITEMS_PER_PAGE]?.toIntOrNull()
            )
            val result = searchVacancyUseCase.search(pager, searchQuery)
            deconstructResult(this, result, HttpStatusCode.OK)
        } catch (e: Throwable) {
            call.respond(
                HttpStatusCode.InternalServerError,
                SerializedException(e.message)
            )
        }
    }
}

private fun Routing.updateVacancy() {
    val updateVacancyUseCase by inject<UpdateVacancyUseCase>()
    put(serverVacancyWithId()) {
        try {
            call.parameters[ID]?.toIntOrNull()
                ?.let { id ->
                    call.receiveNullable<VacancyRequest>()
                        ?.let { vacancy ->
                            val result = updateVacancyUseCase.update(id, vacancy)
                            deconstructResult(this, result, HttpStatusCode.OK)
                        }
                        ?: call.respond(
                            HttpStatusCode.BadRequest,
                            "Invalid or missing VacancyRequest"
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

private fun Routing.updateStatus() {
    val updateVacancyStatusUseCase by inject<UpdateVacancyStatusUseCase>()
    put(serverVacancyUpdateStatus()) {
        try {
            call.parameters[ID]?.toIntOrNull()
                ?.let {id ->
                    call.receiveNullable<VacancyRequestStatus>()
                        ?.let { status ->
                            val result = updateVacancyStatusUseCase.updateStatus(id, status.status)
                            deconstructResult(this, result, HttpStatusCode.OK)
                        }
                        ?: call.respond(
                            HttpStatusCode.BadRequest,
                            "Invalid or missing VacancyRequestStatus"
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
