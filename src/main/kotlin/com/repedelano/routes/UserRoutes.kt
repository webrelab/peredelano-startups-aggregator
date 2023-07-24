package com.repedelano.routes

import com.repedelano.deconstructResult
import com.repedelano.dtos.Pager
import com.repedelano.dtos.SerializedException
import com.repedelano.dtos.user.UserRequest
import com.repedelano.dtos.user.UserSearchRequest
import com.repedelano.routes.PagerRoutes.Companion.ITEMS_PER_PAGE
import com.repedelano.routes.PagerRoutes.Companion.PAGE
import com.repedelano.routes.PagerRoutes.Companion.clientAddPager
import com.repedelano.routes.RouteConstants.Companion.API_V1
import com.repedelano.routes.RouteConstants.Companion.ID
import com.repedelano.routes.UserRoutes.Companion.ADD_USER
import com.repedelano.routes.UserRoutes.Companion.EMAIL
import com.repedelano.routes.UserRoutes.Companion.LAST_NAME
import com.repedelano.routes.UserRoutes.Companion.NAME
import com.repedelano.routes.UserRoutes.Companion.PASSPORT_ID
import com.repedelano.routes.UserRoutes.Companion.TG_USER
import com.repedelano.routes.UserRoutes.Companion.USERS
import com.repedelano.routes.UserRoutes.Companion.serverUserWithId
import com.repedelano.usecases.AddUserUseCase
import com.repedelano.usecases.GetUserByIdUseCase
import com.repedelano.usecases.SearchUserUseCase
import com.repedelano.usecases.UpdateUserUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receiveNullable
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import org.koin.ktor.ext.inject

class UserRoutes {

    companion object {
        const val USER = "$API_V1/user"
        const val USERS = "$API_V1/users"
        const val ADD_USER = "$USER/add"

        const val PASSPORT_ID = "passportId"
        const val NAME = "name"
        const val LAST_NAME = "lastName"
        const val EMAIL = "email"
        const val TG_USER = "tgUser"

        fun serverUserWithId() = "$USER/{$ID}"
        fun clientUserWithId(id: Any?) = id?.let { "$USER/$it" } ?: USER

        fun clientUsersPage(page: Any?, itemsPerPage: Any?) = clientAddPager(USERS, page, itemsPerPage)

        fun clientUsersSearch(
            passportId: Any? = null,
            name: Any? = null,
            lastName: Any? = null,
            email: Any? = null,
            tgUser: Any? = null,
            page: Any? = null,
            itemsPerPage: Any? = null
        ) = listOfNotNull(
            passportId?.let { "$PASSPORT_ID=$passportId" },
            name?.let { "$NAME=$name" },
            lastName?.let { "$LAST_NAME=$lastName" },
            email?.let { "$EMAIL=$email" },
            tgUser?.let { "$TG_USER=$tgUser" }
        ).joinToString("&")
            .let {
                if (it.isBlank()) USERS
                else "$USERS?$it"
            }
            .let { clientAddPager(it, page, itemsPerPage) }
    }
}

fun Routing.userRoutes() {
    addUser()
    getUserById()
    searchUsers()
    updateUser()
}

private fun Routing.addUser() {
    val addIdeaUseCase by inject<AddUserUseCase>()
    post(ADD_USER) {
        try {
            call.receiveNullable<UserRequest>()
                ?.let { user ->
                    val result = addIdeaUseCase.addUser(user)
                    deconstructResult(this, result, HttpStatusCode.Created)
                }
                ?: call.respond(
                    HttpStatusCode.BadRequest,
                    call.receiveText()
                )
        } catch (e: Throwable) {
            call.respond(
                HttpStatusCode.InternalServerError,
                SerializedException(e.message)
            )
        }
    }
}

private fun Routing.getUserById() {
    val getUserByIdUseCase by inject<GetUserByIdUseCase>()
    get(serverUserWithId()) {
        try {
            call.parameters[ID]?.toIntOrNull()
                ?.let { id ->
                    val result = getUserByIdUseCase.get(id)
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

private fun Routing.searchUsers() {
    val searchUserUseCase by inject<SearchUserUseCase>()
    get(USERS) {
        try {
            val pager = Pager.of(
                call.parameters[PAGE]?.toIntOrNull(),
                call.parameters[ITEMS_PER_PAGE]?.toIntOrNull()
            )
            val userSearch = UserSearchRequest(
                passportId = call.parameters[PASSPORT_ID],
                name = call.parameters[NAME],
                lastName = call.parameters[LAST_NAME],
                email = call.parameters[EMAIL],
                tgUser = call.parameters[TG_USER]
            )
            val result = searchUserUseCase.search(pager, userSearch)
            deconstructResult(this, result, HttpStatusCode.OK)
        } catch (e: Throwable) {
            call.respond(HttpStatusCode.InternalServerError, SerializedException(e.message))
        }
    }
}

private fun Routing.updateUser() {
    val updateUserUseCase by inject<UpdateUserUseCase>()
    put(serverUserWithId()) {
        try {
            call.parameters[ID]?.toIntOrNull()
                ?.let { id ->
                    call.receiveNullable<UserRequest>()
                        ?.let { user ->
                            val result = updateUserUseCase.update(id, user)
                            deconstructResult(this, result, HttpStatusCode.OK)
                        }
                        ?: call.respond(
                            HttpStatusCode.BadRequest,
                            "Invalid or missing UserRequest"
                        )
                }
                ?: call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid or missing ID"
                )
        } catch (e: Throwable) {
            call.respond(HttpStatusCode.InternalServerError, SerializedException(e.message))
        }
    }
}