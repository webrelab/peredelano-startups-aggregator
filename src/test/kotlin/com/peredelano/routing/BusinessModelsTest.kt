package com.peredelano.routing

import com.peredelano.BaseTest
import com.peredelano.datageneration.createBusinessModels
import com.peredelano.datageneration.getFakeBusinessModel
import com.peredelano.ext.assertAll
import com.peredelano.ext.getConverted
import com.peredelano.ext.postConverted
import com.peredelano.ext.putOk
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
        fun businessModelsSetUp() = createBusinessModels(10, testEngine)
    }

    @Test
    fun addBusinessModel() = with(testEngine) {
        val bm = getFakeBusinessModel()
        val response: BusinessModelResponse = postConverted(ADD_BM, bm)
        response.assertAll(bm)
    }

    @Test
    fun getById() = with(testEngine) {
        val bm = getFakeBusinessModel()
        val createdId = postConverted<BusinessModelRequest, BusinessModelResponse>(ADD_BM, bm).id
        val response: BusinessModelResponse = getConverted(clientBmWithId(createdId))
        response.assertAll(bm)
    }

    @Test
    fun getAll() = with(testEngine) {
        val all: BusinessModelResponseList = getConverted(BMS)
        assertTrue(all.count >= 10)
    }

    @Test
    fun update() = with(testEngine) {
        val bm = getFakeBusinessModel()
        putOk(clientBmWithId(6), bm)
        val response: BusinessModelResponse = getConverted(clientBmWithId(6))
        response.assertAll(bm)
    }
}