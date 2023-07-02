package com.repedelano.orm.businessmodel

import org.jetbrains.exposed.dao.id.IntIdTable

object BusinessModels : IntIdTable(name = "business-models") {

    val value = varchar("value", 100).uniqueIndex()
    val description = text("description")
}