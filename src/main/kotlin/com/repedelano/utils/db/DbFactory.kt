package com.repedelano.utils.db

import io.ktor.server.application.ApplicationEnvironment
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database

private val logger = KotlinLogging.logger { }

class DbFactory(private val dbConfig: DbConfig) {

    fun connect() {
        logger.info { "Initializing DB connection" }
        Database.connect(
            url = dbConfig.url,
            driver = dbConfig.driver,
            user = dbConfig.username,
            password = dbConfig.password,
        )
        logger.info { "DB initialization complete" }
    }

}

fun ApplicationEnvironment.dbConfig(path: String): DbConfig = with(config.config(path)) {
    DbConfig(
        driver = property("driver").getString(),
        url = property("url").getString(),
        username = property("username").getString(),
        password = property("pwd").getString(),
        maxPoolSize = property("maxPoolSize").getString().toInt()
    )
}