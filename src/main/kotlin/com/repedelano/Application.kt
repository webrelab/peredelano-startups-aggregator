package com.repedelano

import com.repedelano.datagenerator.RequestGenerators
import com.repedelano.plugins.configure
import com.repedelano.plugins.configureRouting
import com.repedelano.plugins.configureSecurity
import com.repedelano.utils.configure
import com.repedelano.utils.db.DbFactory
import com.repedelano.utils.db.dbConfig
import com.repedelano.utils.environmentSetter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.koin.core.parameter.parametersOf
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

private val logger = KotlinLogging.logger { }

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {

    install(Koin) {
        configure(environmentSetter(environment))
    }
    logger.info { "Koin installed" }
    val dbFactory by inject<DbFactory> { parametersOf(environment.dbConfig("database")) }
    val json by inject<Json>()

    install(ContentNegotiation) {
        json(json)
    }
    logger.info { "ContentNegotiation installed" }

    install(StatusPages) {
        configure()
    }
    logger.info { "StatusPages installed" }

    configureSecurity()
    logger.info { "Security configured" }
    configureRouting()
    logger.info { "Routing configured" }

    dbFactory.connect()

    if (environment.config.property("ktor.environment").getString() == "dev") {
        with(RequestGenerators) {
            generateScopes(25)
            generateBusinessModels(4)
            generateTechnologies(48)
            generateUsers(180)
            generateIdeas(120)
        }
    }
}