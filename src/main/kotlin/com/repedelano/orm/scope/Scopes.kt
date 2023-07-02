package com.repedelano.orm.scope

import org.jetbrains.exposed.dao.id.IntIdTable

object Scopes : IntIdTable(name = "scopes") {

    val value = varchar("value", 100).uniqueIndex()
    val description = text("description")
}