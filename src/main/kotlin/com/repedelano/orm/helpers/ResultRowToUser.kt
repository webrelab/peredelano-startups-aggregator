package com.repedelano.orm.helpers

import com.repedelano.dtos.user.UserResponse
import com.repedelano.dtos.user.UserResponseList
import com.repedelano.orm.user.Users
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toUser(): UserResponse {
    return with(Users) {
        UserResponse(
            id = get(id).value,
            passportId = get(passportId),
            name = get(name),
            lastName = get(lastName),
            email = get(email),
            tgUser = get(tgUser),
            picture = get(picture),
            registered = get(registered).toString()
        )
    }
}

fun List<UserResponse>.toUserResponseList(page: Int, total: Int): UserResponseList {
    return UserResponseList(this, size, page, total)
}