package com.peredelano.routing

import com.peredelano.BaseTest
import com.peredelano.ext.getOk
import com.repedelano.routes.RouteConstants.Companion.API_V1
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RootTest : BaseTest() {

    @Test
    fun testRoot() = with(testEngine) {
        val response = getOk("/")
        Assertions.assertEquals("Hello dude!", response)
    }

    @Test
    fun testApiV1() = with(testEngine) {
        val response = getOk(API_V1)
        Assertions.assertEquals("It's API v1 endpoint", response)
    }
}