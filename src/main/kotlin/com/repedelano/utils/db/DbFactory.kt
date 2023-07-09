package com.repedelano.utils.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.ApplicationEnvironment
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database

private val logger = KotlinLogging.logger { }

class DbFactory(private val dbConfig: DbConfig) {

    fun connect() {
        logger.info { "Initializing DB connection" }
        Database.connect(hikari())
        logger.info { "DB initialization complete" }
        SchemaCreation.createSchema()
        logger.info { "Schema creation complete" }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = dbConfig.driver
        config.jdbcUrl = dbConfig.url
        config.username = dbConfig.username
        config.password = dbConfig.password
        config.maximumPoolSize = dbConfig.maxPoolSize
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
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