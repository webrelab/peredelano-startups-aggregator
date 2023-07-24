package com.peredelano.routing

import com.peredelano.BaseTest
import com.peredelano.ext.assertAll
import com.peredelano.ext.getConverted
import com.peredelano.ext.postConverted
import com.peredelano.ext.postCreated
import com.peredelano.ext.putOk
import com.repedelano.datagenerator.RequestGenerators.getFakeUser
import com.repedelano.dtos.user.UserRequest
import com.repedelano.dtos.user.UserResponse
import com.repedelano.dtos.user.UserResponseList
import com.repedelano.routes.UserRoutes.Companion.ADD_USER
import com.repedelano.routes.UserRoutes.Companion.USERS
import com.repedelano.routes.UserRoutes.Companion.clientUserWithId
import com.repedelano.routes.UserRoutes.Companion.clientUsersPage
import com.repedelano.routes.UserRoutes.Companion.clientUsersSearch
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class UsersTest : BaseTest() {

    companion object {

        @JvmStatic
        @BeforeAll
        fun usersSetUp() = repeat(30) {
            testEngine.postCreated(ADD_USER, getFakeUser())
        }
    }

    @Test
    fun createUser() = with(testEngine) {
        val user = getFakeUser()
        val body: UserResponse = postConverted(ADD_USER, user)
        body.assertAll(user)
        assertTrue(body.id > 0)
    }

    @Test
    fun getUserById() = with(testEngine) {
        val data = getFakeUser()
        val createdId = postConverted<UserRequest, UserResponse>(ADD_USER, data).id
        val user: UserResponse = getConverted(clientUserWithId(createdId))
        user.assertAll(data)
    }

    @Test
    fun `should load only 20 user`() = with(testEngine) {
        val body = getConverted<UserResponseList>(USERS)
        assertEquals(body.users.size, 20)
    }

    @Test
    fun `check last page`() = with(testEngine) {
        val total = getConverted<UserResponseList>(clientUsersPage(0, 20)).total
        val lastPage: UserResponseList = getConverted(clientUsersPage(total / 20, 20))
        assertEquals(total % 20, lastPage.count)
    }

    @Test
    fun `user not found`() = with(testEngine) {
        val user = getConverted<UserResponse>(clientUserWithId(3))
        assertEquals(3, user.id)
        val notFound = handleRequest(HttpMethod.Get, clientUserWithId(3000)) {}.response
        assertEquals(HttpStatusCode.NotFound, notFound.status())
    }

    @Test
    fun `user should be updated`() = with(testEngine) {
        val dataForUpdate = getFakeUser()
        putOk(clientUserWithId(12), dataForUpdate)
        val updated = getConverted<UserResponse>(clientUserWithId(12))
        updated.assertAll(dataForUpdate)
    }

    @Test
    fun searchUserAllFields() = with(testEngine) {
        val data = getFakeUser()
        postCreated(ADD_USER, data)
        val found: UserResponseList = getConverted(
            clientUsersSearch(
                passportId = data.passportId,
                tgUser = data.tgUser,
                name = data.name,
                lastName = data.lastName,
                email = data.email,
            )
        )
        assertEquals(1, found.count)
        found.users[0].assertAll(data)
    }

    @Test
    fun searchUserCustomFields() = with(testEngine) {
        val data = getFakeUser()
        postCreated(ADD_USER, data)
        val found1: UserResponseList = getConverted(
            clientUsersSearch(
                passportId = data.passportId,
                tgUser = data.tgUser
            )
        )
        assertEquals(1, found1.count)
        found1.users[0].assertAll(data)
        val found2: UserResponseList = getConverted(
            clientUsersSearch(
                name = data.name,
                email = data.email
            )
        )
        assertEquals(1, found2.count)
        found2.users[0].assertAll(data)
    }

    @Test
    fun searchUserWithPartiallyFields() = with(testEngine) {
        val data = getFakeUser()
        postCreated(ADD_USER, data)
        val partiallyEmail = data.email.subSequence(2, data.email.length - 2)
        val partiallyName = data.name.subSequence(1, data.name.length - 1)
        val found: UserResponseList = getConverted(
            clientUsersSearch(
                email = partiallyEmail,
                name = partiallyName
            )
        )
        assertEquals(1, found.count)
        found.users[0].assertAll(data)
    }

    @Test
    fun `user not found with search`() = with(testEngine) {
        val data = getFakeUser()
        postCreated(ADD_USER, data)
        val notFound1: UserResponseList = getConverted(
            clientUsersSearch(
                name = "wrong name"
            )
        )
        assertEquals(0, notFound1.count)
        val notFound2: UserResponseList = getConverted(
            clientUsersSearch(
                name = data.name,
                lastName = "wrong lastname"
            )
        )
        assertEquals(0, notFound2.count)
    }

    @Test
    fun `should get multiple result`() = with(testEngine) {
        val data = listOf(
            getFakeUser(name = "Tarantino"),
            getFakeUser(name = "Torantino"),
            getFakeUser(name = "Turantino")
        )
        data.forEach { postCreated(ADD_USER, it) }
        val found: UserResponseList = getConverted(clientUsersSearch(name = "ranti"))
        assertEquals(3, found.count)
        data.forEach { d ->
            val user = found.users.find { it.name == d.name }
            assertNotNull(user, "User ${d.name} not found")
            user!!.assertAll(d)
        }
    }
}