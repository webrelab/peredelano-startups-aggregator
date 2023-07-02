package com.repedelano.orm.technology

import org.jetbrains.exposed.dao.id.IntIdTable

object Technologies : IntIdTable(name = "technologies") {

    val value = varchar("value", 100)
}