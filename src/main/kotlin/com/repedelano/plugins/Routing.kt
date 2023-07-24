package com.repedelano.plugins

import com.repedelano.routes.RouteConstants.Companion.API_V1
import com.repedelano.routes.businessModelRoutes
import com.repedelano.routes.ideaRoutes
import com.repedelano.routes.projectRolesRoutes
import com.repedelano.routes.scopeRoutes
import com.repedelano.routes.technologyRoutes
import com.repedelano.routes.userRoutes
import com.repedelano.routes.vacancyRoutes
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        ideaRoutes()
        userRoutes()
        businessModelRoutes()
        projectRolesRoutes()
        scopeRoutes()
        technologyRoutes()
        vacancyRoutes()
        get("/") {
            call.respond(HttpStatusCode.OK, "Hello dude!")
        }
        get(API_V1) {
            call.respond(HttpStatusCode.OK, "It's API v1 endpoint")
        }
    }
}
