package com.peredelano.routing

import com.peredelano.BaseTest
import com.peredelano.ext.getConverted
import com.repedelano.datagenerator.IdGen
import com.repedelano.datagenerator.RequestGenerators
import com.repedelano.dtos.vacancy.VacancyResponse
import com.repedelano.dtos.vacancy.VacancyResponseList
import com.repedelano.routes.VacancyRoutes.Companion.clientVacancySearch
import com.repedelano.routes.VacancyRoutes.Companion.clientVacancyWithId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test

class VacancySearchTest : BaseTest() {

    companion object {

        @JvmStatic
        @BeforeAll
        fun vacancySetUp() = with(RequestGenerators) {
            generateScopes(10)
            generateBusinessModels(10)
            generateTechnologies(10)
            generateUsers(30)
            generateProjectRoles(10)
            generateIdeas(500)
            generateVacancies(2000)
        }
    }

    @RepeatedTest(100)
    fun searchByIdeaId() = with(testEngine) {
        val ideaId = getConverted<VacancyResponse>(
            clientVacancyWithId(
                IdGen.nextUniqueId("vacancy", 2000)
            )
        ).ideaId
        val result: VacancyResponseList = getConverted(
            clientVacancySearch(
                ideaId = ideaId
            )
        )
        assertTrue(result.vacancies.isNotEmpty(), "Vacancies not found")
        assertTrue(
            result.vacancies.all { it.ideaId == ideaId }
        )
    }

    @RepeatedTest(30)
    fun searchByProjectRole() = with(testEngine) {
        val projectRole = IdGen.nextId(10)
        val result: VacancyResponseList = getConverted(
            clientVacancySearch(
                projectRole = projectRole
            )
        )
        assertTrue(result.vacancies.isNotEmpty(), "Vacancies not found")
        assertTrue(
            result.vacancies.all { it.projectRole.id == projectRole }
        )
    }

    @RepeatedTest(300)
    fun searchByTechStack() = with(testEngine) {
        val techStack = IdGen.nextUniqueIdList(4, 10)
        val result: VacancyResponseList = getConverted(
            clientVacancySearch(
                techStack = techStack
            )
        )
        assertTrue(result.vacancies.isNotEmpty(), "Vacancies not found")
        assertTrue(
            result.vacancies.all {response ->
                response.techStack.map { it.id }.containsAll(techStack)
            }
        )
    }

    @Test
    fun searchByDescription() = with(testEngine) {
        val word = "everybody"
        val result: VacancyResponseList = getConverted(
            clientVacancySearch(
                description = word
            )
        )
        assertTrue(result.vacancies.isNotEmpty(), "Vacancies not found")
        assertTrue(
            result.vacancies.all {
                it.description.lowercase().contains(word)
            }
        )
    }
}