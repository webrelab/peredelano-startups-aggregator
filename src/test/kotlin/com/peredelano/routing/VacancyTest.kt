package com.peredelano.routing

import com.peredelano.BaseTest
import com.peredelano.ext.assertAll
import com.peredelano.ext.getConverted
import com.peredelano.ext.postConverted
import com.peredelano.ext.putConverted
import com.repedelano.datagenerator.RequestGenerators
import com.repedelano.datagenerator.RequestGenerators.getFakeVacancy
import com.repedelano.dtos.idea.IdeaResponseList
import com.repedelano.dtos.vacancy.VacancyRequest
import com.repedelano.dtos.vacancy.VacancyRequestStatus
import com.repedelano.dtos.vacancy.VacancyResponse
import com.repedelano.dtos.vacancy.VacancyResponseList
import com.repedelano.dtos.vacancy.VacancyStatus
import com.repedelano.routes.IdeaRoutes.Companion.clientGetPage
import com.repedelano.routes.VacancyRoutes.Companion.ADD_VAC
import com.repedelano.routes.VacancyRoutes.Companion.clientVacancySearch
import com.repedelano.routes.VacancyRoutes.Companion.clientVacancyUpdateStatus
import com.repedelano.routes.VacancyRoutes.Companion.clientVacancyWithId
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

private val logger = KotlinLogging.logger { }

class VacancyTest : BaseTest() {

    companion object {

        @JvmStatic
        @BeforeAll
        fun vacancySetUp() = with(RequestGenerators) {
            generateScopes(10)
            generateBusinessModels(5)
            generateTechnologies(10)
            generateUsers(30)
            generateProjectRoles(10)
            generateIdeas(50)
            generateVacancies(100)
        }
    }

    @Test
    fun spreadingVacancy() = with(testEngine) {
        val ids = getConverted<IdeaResponseList>(clientGetPage(0, 100))
            .ideas.map { it.id }

        val spread = ids.map {
            val vacancies: VacancyResponseList = getConverted(
                clientVacancySearch(
                    ideaId = it
                )
            )
            "$it: ${vacancies.total}"
        }
        logger.info { spread.joinToString("\n") }
    }

    @Test
    fun addVacancy() = with(testEngine) {
        val data = getFakeVacancy()
        val response: VacancyResponse = postConverted(ADD_VAC, data)
        response.assertAll(data)
    }

    @Test
    fun selectById() = with(testEngine) {
        val data = getFakeVacancy()
        val responseId = postConverted<VacancyRequest, VacancyResponse>(ADD_VAC, data).id
        val response: VacancyResponse = getConverted(clientVacancyWithId(responseId))
        response.assertAll(data)
    }

    @Test
    fun updateVacancy() = with(testEngine) {
        val data = getFakeVacancy()
        val response: VacancyResponse = putConverted(clientVacancyWithId(4), data)
        response.assertAll(data)
    }

    @Test
    fun updateStatusToClosed() = with(testEngine) {
        val openStatusVacancyId = getConverted<VacancyResponseList>(
            clientVacancySearch(
                status = VacancyStatus.OPEN
            )
        ).vacancies[0].id
        val response: VacancyResponse = putConverted(
            clientVacancyUpdateStatus(openStatusVacancyId), VacancyRequestStatus(VacancyStatus.CLOSED)
        )
        assertEquals(VacancyStatus.CLOSED, response.status)
    }

    @Test
    fun updateStatusToDeclined() = with(testEngine) {
        val openStatusVacancyId = getConverted<VacancyResponseList>(
            clientVacancySearch(
                status = VacancyStatus.OPEN
            )
        ).vacancies[0].id
        val response: VacancyResponse = putConverted(
            clientVacancyUpdateStatus(openStatusVacancyId), VacancyRequestStatus(VacancyStatus.DECLINED)
        )
        assertEquals(VacancyStatus.DECLINED, response.status)
    }


}