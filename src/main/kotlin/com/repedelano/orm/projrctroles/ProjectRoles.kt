package com.repedelano.orm.projrctroles

import org.jetbrains.exposed.dao.id.IntIdTable

object ProjectRoles : IntIdTable("project_roles") {

    val name = varchar("name", 100).uniqueIndex()
    val description = text("description").nullable()
}