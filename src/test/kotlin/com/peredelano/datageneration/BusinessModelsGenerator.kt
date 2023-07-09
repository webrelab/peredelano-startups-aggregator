package com.peredelano.datageneration

import com.peredelano.BaseTest
import com.peredelano.ext.postCreated
import com.repedelano.dtos.businessmodel.BusinessModelRequest
import com.repedelano.routes.BusinessModelRoutes.Companion.ADD_BM
import io.github.serpro69.kfaker.Faker
import io.ktor.server.testing.TestApplicationEngine

fun createBusinessModels(count: Int, testEngine: TestApplicationEngine) =
    repeat(count) {testEngine.postCreated(ADD_BM, getFakeBusinessModel())}

fun getFakeBusinessModel(
    value: String = BaseTest.faker.commerce.productName(),
    description: String = Faker().bigBangTheory.quotes()
) = BusinessModelRequest(value, description)