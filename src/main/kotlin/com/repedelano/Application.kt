package com.repedelano

import com.repedelano.plugins.configure
import com.repedelano.plugins.configureRouting
import com.repedelano.plugins.configureSecurity
import com.repedelano.utils.configure
import com.repedelano.utils.db.DbFactory
import com.repedelano.utils.db.dbConfig
import com.repedelano.utils.koinModules
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module(koinModules: List<Module> = koinModules()) {
    install(Koin) {
        configure(koinModules)
    }

    val dbFactory by inject<DbFactory> { parametersOf(environment.dbConfig("ktor.database")) }
    val json by inject<Json>()

    install(ContentNegotiation) {
        json(json)
    }

    install(StatusPages) {
        configure()
    }

    configureSecurity()
    configureRouting()

    dbFactory.connect()
}
