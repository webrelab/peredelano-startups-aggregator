package com.peredelano.datageneration

import com.peredelano.BaseTest
import com.peredelano.ext.postCreated
import com.repedelano.dtos.user.UserRequest
import com.repedelano.routes.UserRoutes.Companion.ADD_USER
import io.ktor.server.testing.TestApplicationEngine
import java.util.UUID

fun createUsers(count: Int, testEngine: TestApplicationEngine) = repeat(count) {
    testEngine.postCreated(ADD_USER, getFakeUser())
}

fun getFakeUser(
    name: String = BaseTest.faker.name.firstName(),
    lastName: String = BaseTest.faker.name.lastName(),
    passportId: String = UUID.randomUUID().toString(),
    email: String = BaseTest.faker.internet.email("$lastName.$name"),
    tgUser: String = "@${lastName}_$name",
    picture: String = BaseTest.faker.name.name()
) = UserRequest(
    passportId = passportId,
    name = name,
    lastName = lastName,
    email = email,
    tgUser = tgUser,
    picture = picture
)