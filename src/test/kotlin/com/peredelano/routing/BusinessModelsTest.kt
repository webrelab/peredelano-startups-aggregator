package com.peredelano.routing

import com.peredelano.BaseTest
import com.peredelano.ext.assertAll
import com.peredelano.ext.getConverted
import com.peredelano.ext.postConverted
import com.peredelano.ext.postCreated
import com.peredelano.ext.putConverted
import com.repedelano.datagenerator.RequestGenerators.getFakeBusinessModel
import com.repedelano.dtos.businessmodel.BusinessModelRequest
import com.repedelano.dtos.businessmodel.BusinessModelResponse
import com.repedelano.dtos.businessmodel.BusinessModelResponseList
import com.repedelano.routes.BusinessModelRoutes.Companion.ADD_BM
import com.repedelano.routes.BusinessModelRoutes.Companion.BMS
import com.repedelano.routes.BusinessModelRoutes.Companion.clientBmWithId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class BusinessModelsTest : BaseTest() {

    companion object {

        @JvmStatic
        @BeforeAll
        fun businessModelsSetUp() = repeat(10) {testEngine.postCreated(ADD_BM, getFakeBusinessModel())}
    }

    @Test
    fun addBusinessModel() = with(testEngine) {
        val data = getFakeBusinessModel()
        val response: BusinessModelResponse = postConverted(ADD_BM, data)
        response.assertAll(data)
    }

    @Test
    fun getById() = with(testEngine) {
        val data = getFakeBusinessModel()
        val createdId = postConverted<BusinessModelRequest, BusinessModelResponse>(ADD_BM, data).id
        val response: BusinessModelResponse = getConverted(clientBmWithId(createdId))
        response.assertAll(data)
    }

    @Test
    fun getAll() = with(testEngine) {
        val all: BusinessModelResponseList = getConverted(BMS)
        assertTrue(all.count >= 10)
    }

    @Test
    fun update() = with(testEngine) {
        val data = getFakeBusinessModel()
        val putResult: BusinessModelResponse = putConverted(clientBmWithId(6), data)
        putResult.assertAll(data)
        val response: BusinessModelResponse = getConverted(clientBmWithId(6))
        response.assertAll(data)
    }
}