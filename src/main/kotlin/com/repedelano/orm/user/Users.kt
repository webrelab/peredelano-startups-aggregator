package com.repedelano.orm.user

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object Users : IntIdTable(name = "users") {

    val passportId = varchar("passport_id", 100).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val name = varchar("name", 100)
    val lastName = varchar("lastName", 100)
    val tgUser = varchar("tg_user", 100)
    val picture = varchar("picture", 255)
    val registered = timestamp("registered")
}