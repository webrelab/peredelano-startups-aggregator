package com.repedelano.utils.db

import com.repedelano.orm.businessmodel.BusinessModels
import com.repedelano.orm.idea.IdeaBusinessModels
import com.repedelano.orm.idea.IdeaScopes
import com.repedelano.orm.idea.IdeaTechnologies
import com.repedelano.orm.idea.Ideas
import com.repedelano.orm.projrctroles.ProjectRoles
import com.repedelano.orm.scope.Scopes
import com.repedelano.orm.technology.Technologies
import com.repedelano.orm.user.Users
import com.repedelano.orm.vacancies.Vacancies
import com.repedelano.orm.vacancies.VacancyTechnologies
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationEnvironment
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

object SchemaCreation {

    private val tables: Array<Table> = arrayOf(
        BusinessModels,
        ProjectRoles,
        Scopes,
        Technologies,
        Users,
        Vacancies,
        VacancyTechnologies,
        Ideas,
        IdeaScopes,
        IdeaBusinessModels,
        IdeaTechnologies,
    )

    fun createSchema() {
        val env by inject<ApplicationEnvironment>(ApplicationEnvironment::class.java)
        transaction {
            if (env.config.property("ktor.dropdatabase").getString().toBoolean()) {
                SchemaUtils.drop(*tables)
            }
            SchemaUtils.create(*tables)
        }
    }
}