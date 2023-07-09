package com.peredelano

import io.github.serpro69.kfaker.Faker
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.createTestEnvironment
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll

abstract class BaseTest {

    companion object {
        lateinit var testEngine: TestApplicationEngine

        val faker = Faker()

        @JvmStatic
        @BeforeAll
        fun setup() {
            testEngine = TestApplicationEngine(createTestEnvironment {
                config = ApplicationConfig("application-test.conf")
            })
            testEngine.start(wait = true)
        }

        @JvmStatic
        @AfterAll
        fun teardown() {
            testEngine.stop(1000, 5000)
        }
    }
}