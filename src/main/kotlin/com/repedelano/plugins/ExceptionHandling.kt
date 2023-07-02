package com.repedelano.plugins

import com.repedelano.dtos.SerializedException
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond

fun StatusPagesConfig.configure() {
    exception<NotFoundException> { call, cause ->
        call.respond(HttpStatusCode.NotFound, SerializedException(cause.localizedMessage))
    }

    exception<Throwable> { call, cause ->
        call.respond(HttpStatusCode.InternalServerError, SerializedException(cause.localizedMessage))
    }
}