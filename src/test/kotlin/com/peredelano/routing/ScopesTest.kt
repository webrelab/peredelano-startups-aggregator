package com.peredelano.routing

import com.peredelano.BaseTest
import com.peredelano.ext.assertAll
import com.peredelano.ext.getConverted
import com.peredelano.ext.postConverted
import com.peredelano.ext.postCreated
import com.peredelano.ext.putConverted
import com.repedelano.datagenerator.RequestGenerators.getFakeScope
import com.repedelano.dtos.scope.ScopeRequest
import com.repedelano.dtos.scope.ScopeResponse
import com.repedelano.dtos.scope.ScopeResponseList
import com.repedelano.routes.ScopeRoutes.Companion.ADD_SCOPE
import com.repedelano.routes.ScopeRoutes.Companion.SCOPES
import com.repedelano.routes.ScopeRoutes.Companion.clientScopeSearch
import com.repedelano.routes.ScopeRoutes.Companion.clientScopeWithId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class ScopesTest : BaseTest() {

    companion object {

        internal val data1 = ScopeRequest(
            "Data1 scope",
            "Far far away, behind the word mountains, far from the countries Vokalia and Consonantia, there live the blind texts"
        )

        internal val data2 = ScopeRequest(
            "Data2 scope",
            "Even the all-powerful Pointing has no control about the blind texts it is an almost unorthographic life One day."
        )

        @JvmStatic
        @BeforeAll
        fun scopeSetUp(): Unit = with(testEngine) {
            repeat(30) { postCreated(ADD_SCOPE, getFakeScope()) }
            postCreated(ADD_SCOPE, data1)
            postCreated(ADD_SCOPE, data2)
        }
    }

    @Test
    fun addScope() = with(testEngine) {
        val data = getFakeScope()
        val response: ScopeResponse = postConverted(ADD_SCOPE, data)
        response.assertAll(data)
    }

    @Test
    fun getById() = with(testEngine) {
        val data = getFakeScope()
        val responseId = postConverted<ScopeRequest, ScopeResponse>(ADD_SCOPE, data).id
        val response: ScopeResponse = getConverted(clientScopeWithId(responseId))
        response.assertAll(data)
    }

    @Test
    fun searchInValue() = with(testEngine) {
        val response: ScopeResponseList = getConverted(clientScopeSearch("data1"))
        assertEquals(1, response.count)
        response.scopes[0].assertAll(data1)
    }

    @Test
    fun searchInDescription() = with(testEngine) {
        val response: ScopeResponseList = getConverted(clientScopeSearch("almost unorthographic"))
        assertEquals(1, response.count)
        response.scopes[0].assertAll(data2)
    }

    @Test
    fun searchMultiple() = with(testEngine) {
        val response: ScopeResponseList = getConverted(clientScopeSearch("blind texts"))
        assertEquals(2, response.count)
        response.scopes.find { it.value == data1.value }?.assertAll(data1)
            ?: throw AssertionError("item 'data1' not found")
        response.scopes.find { it.value == data2.value }?.assertAll(data2)
            ?: throw AssertionError("item 'data2' not found")
    }

    @Test
    fun getAll() = with(testEngine) {
        val response: ScopeResponseList = getConverted(SCOPES)
        assertTrue(response.count >= 30)
    }

    @Test
    fun update() = with(testEngine) {
        val data = getFakeScope()
        val putResult: ScopeResponse = putConverted(clientScopeWithId(6), data)
        putResult.assertAll(data)
        val response: ScopeResponse = getConverted(clientScopeWithId(6))
        response.assertAll(data)
    }
}