package com.peredelano.routing

import com.peredelano.BaseTest
import com.peredelano.ext.assertAll
import com.peredelano.ext.getConverted
import com.peredelano.ext.postConverted
import com.peredelano.ext.postCreated
import com.peredelano.ext.putConverted
import com.repedelano.datagenerator.RequestGenerators.getFakeTechnology
import com.repedelano.dtos.technology.TechnologyRequest
import com.repedelano.dtos.technology.TechnologyResponse
import com.repedelano.dtos.technology.TechnologyResponseList
import com.repedelano.routes.TechnologyRoutes.Companion.ADD_TECH
import com.repedelano.routes.TechnologyRoutes.Companion.TECHS
import com.repedelano.routes.TechnologyRoutes.Companion.clientSearchTechnology
import com.repedelano.routes.TechnologyRoutes.Companion.clientTechnologyWithId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class TechnologiesTest : BaseTest() {

    companion object {
        val data1 = TechnologyRequest("Technology variant one")
        val data2 = TechnologyRequest("Technology variant two")

        @JvmStatic
        @BeforeAll
        fun technologySetUp(): Unit = with(testEngine) {
            repeat(30) { testEngine.postCreated(ADD_TECH, getFakeTechnology())}
            postCreated(ADD_TECH, data1)
            postCreated(ADD_TECH, data2)
        }
    }

    @Test
    fun addTechnology() = with(testEngine) {
        val data = getFakeTechnology()
        val result: TechnologyResponse = postConverted(ADD_TECH, data)
        result.assertAll(data)
    }

    @Test
    fun getTechnologyById() = with(testEngine) {
        val data = getFakeTechnology()
        val technologyId = postConverted<TechnologyRequest, TechnologyResponse>(ADD_TECH, data).id
        val response: TechnologyResponse = getConverted(clientTechnologyWithId(technologyId))
        response.assertAll(data)
    }

    @Test
    fun multipleSearch() = with(testEngine) {
        val search: TechnologyResponseList =
            getConverted(clientSearchTechnology("technology variant"))
        assertEquals(2, search.count)
        search.technologies.find { it.value == data1.value }?.assertAll(data1)
            ?: throw AssertionError("item 'data1' not found")
        search.technologies.find { it.value == data2.value }?.assertAll(data2)
            ?: throw AssertionError("item 'data2' not found")
    }

    @Test
    fun singleSearch() = with(testEngine) {
        val search: TechnologyResponseList =
            getConverted(clientSearchTechnology("variant one"))
        assertEquals(1, search.count)
        search.technologies[0].assertAll(data1)
    }

    @Test
    fun notFound() = with(testEngine) {
        val search: TechnologyResponseList =
            getConverted(clientSearchTechnology("wrong query"))
        assertEquals(0, search.count)
    }

    @Test
    fun getAll() = with(testEngine) {
        val response: TechnologyResponseList =
            getConverted(TECHS)
        assertTrue(response.count >= 30)
    }

    @Test
    fun update() = with(testEngine) {
        val data = getFakeTechnology()
        val putResult: TechnologyResponse = putConverted(clientTechnologyWithId(8), data)
        putResult.assertAll(data)
        val response: TechnologyResponse = getConverted(clientTechnologyWithId(8))
        response.assertAll(data)
    }
}