package com.peredelano.routing

import com.peredelano.BaseTest
import com.peredelano.ext.getConverted
import com.peredelano.ext.postConverted
import com.peredelano.ext.putConverted
import com.peredelano.ext.putOk
import com.repedelano.datagenerator.RequestGenerators
import com.repedelano.datagenerator.RequestGenerators.getFakeIdea
import com.repedelano.dtos.idea.IdeaRequest
import com.repedelano.dtos.idea.IdeaResponse
import com.repedelano.dtos.idea.IdeaStage
import com.repedelano.dtos.idea.IdeaStageRequest
import com.repedelano.dtos.idea.IdeaStatus
import com.repedelano.routes.IdeaRoutes.Companion.ADD_IDEA
import com.repedelano.routes.IdeaRoutes.Companion.clientIdeaWithId
import com.repedelano.routes.IdeaRoutes.Companion.clientUpdateStage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class IdeaStatusTest : BaseTest() {

    companion object {

        @JvmStatic
        @BeforeAll
        fun ideasSetUp(): Unit = with(RequestGenerators) {
            generateScopes(20)
            generateBusinessModels(4)
            generateTechnologies(80)
            generateUsers(40)
            generateIdeas(30)
        }

        @JvmStatic
        fun updateData(): List<Arguments> = listOf(
            Arguments.of(
                IdeaRequest(title = ""),
                IdeaStatus.SEARCH_SOLUTION
            ),
            Arguments.of(
                IdeaRequest(tgLink = ""),
                IdeaStatus.SEARCH_SOLUTION
            ),
            Arguments.of(
                IdeaRequest(scopes = listOf()),
                IdeaStatus.COLLECT_SOCIO_ECONOMIC_INFO
            ),
            Arguments.of(
                IdeaRequest(description = ""),
                IdeaStatus.SEARCH_SOLUTION
            ),
            Arguments.of(
                IdeaRequest(businessModels = listOf()),
                IdeaStatus.FINANCIAL_JUSTIFICATION_REQUIRED
            ),
            Arguments.of(
                IdeaRequest(similarProjects = ""),
                IdeaStatus.COLLECT_SOCIO_ECONOMIC_INFO
            ),
            Arguments.of(
                IdeaRequest(targetAudience = ""),
                IdeaStatus.COLLECT_SOCIO_ECONOMIC_INFO
            ),
            Arguments.of(
                IdeaRequest(marketResearch = ""),
                IdeaStatus.COLLECT_SOCIO_ECONOMIC_INFO
            ),
            Arguments.of(
                IdeaRequest(businessPlan = ""),
                IdeaStatus.FINANCIAL_JUSTIFICATION_REQUIRED
            ),
            Arguments.of(
                IdeaRequest(techStack = listOf()),
                IdeaStatus.SEARCH_TECHNOLOGICAL_SOLUTION
            ),
            Arguments.of(
                IdeaRequest(resources = ""),
                IdeaStatus.SEARCH_TECHNOLOGICAL_SOLUTION
            )
        )

        @JvmStatic
        fun createData(): List<Arguments> = listOf(
            Arguments.of(
                IdeaRequest(
                    owner = 1,
                    problem = "text",
                    title = "",
                    tgLink = "",
                    description = "",
                    scopes = listOf(),
                    similarProjects = "",
                    targetAudience = "",
                    marketResearch = "",
                    businessModels = listOf(),
                    businessPlan = "",
                    techStack = listOf(),
                    resources = ""
                ),
                IdeaStatus.SEARCH_SOLUTION
            ),
            Arguments.of(
                IdeaRequest(
                    owner = 1,
                    problem = "text",
                    title = "text",
                    tgLink = "",
                    description = "",
                    scopes = listOf(),
                    similarProjects = "",
                    targetAudience = "",
                    marketResearch = "",
                    businessModels = listOf(),
                    businessPlan = "",
                    techStack = listOf(),
                    resources = ""
                ),
                IdeaStatus.SEARCH_SOLUTION
            ),
            Arguments.of(
                IdeaRequest(
                    owner = 1,
                    problem = "text",
                    title = "text",
                    tgLink = "text",
                    description = "",
                    scopes = listOf(),
                    similarProjects = "",
                    targetAudience = "",
                    marketResearch = "",
                    businessModels = listOf(),
                    businessPlan = "",
                    techStack = listOf(),
                    resources = ""
                ),
                IdeaStatus.SEARCH_SOLUTION
            ),
            Arguments.of(
                IdeaRequest(
                    owner = 1,
                    problem = "text",
                    title = "text",
                    tgLink = "text",
                    description = "text",
                    scopes = listOf(),
                    similarProjects = "",
                    targetAudience = "",
                    marketResearch = "",
                    businessModels = listOf(),
                    businessPlan = "",
                    techStack = listOf(),
                    resources = ""
                ),
                IdeaStatus.COLLECT_SOCIO_ECONOMIC_INFO
            ),
            Arguments.of(
                IdeaRequest(
                    owner = 1,
                    problem = "text",
                    title = "text",
                    tgLink = "text",
                    description = "text",
                    scopes = listOf(1),
                    similarProjects = "",
                    targetAudience = "",
                    marketResearch = "",
                    businessModels = listOf(),
                    businessPlan = "",
                    techStack = listOf(),
                    resources = ""
                ),
                IdeaStatus.COLLECT_SOCIO_ECONOMIC_INFO
            ),
            Arguments.of(
                IdeaRequest(
                    owner = 1,
                    problem = "text",
                    title = "text",
                    tgLink = "text",
                    description = "text",
                    scopes = listOf(1),
                    similarProjects = "text",
                    targetAudience = "",
                    marketResearch = "",
                    businessModels = listOf(),
                    businessPlan = "",
                    techStack = listOf(),
                    resources = ""
                ),
                IdeaStatus.COLLECT_SOCIO_ECONOMIC_INFO
            ),
            Arguments.of(
                IdeaRequest(
                    owner = 1,
                    problem = "text",
                    title = "text",
                    tgLink = "text",
                    description = "text",
                    scopes = listOf(1),
                    similarProjects = "text",
                    targetAudience = "text",
                    marketResearch = "",
                    businessModels = listOf(),
                    businessPlan = "",
                    techStack = listOf(),
                    resources = ""
                ),
                IdeaStatus.COLLECT_SOCIO_ECONOMIC_INFO
            ),
            Arguments.of(
                IdeaRequest(
                    owner = 1,
                    problem = "text",
                    title = "text",
                    tgLink = "text",
                    description = "text",
                    scopes = listOf(1),
                    similarProjects = "text",
                    targetAudience = "text",
                    marketResearch = "text",
                    businessModels = listOf(),
                    businessPlan = "",
                    techStack = listOf(),
                    resources = ""
                ),
                IdeaStatus.FINANCIAL_JUSTIFICATION_REQUIRED
            ),
            Arguments.of(
                IdeaRequest(
                    owner = 1,
                    problem = "text",
                    title = "text",
                    tgLink = "text",
                    description = "text",
                    scopes = listOf(1),
                    similarProjects = "text",
                    targetAudience = "text",
                    marketResearch = "text",
                    businessModels = listOf(1),
                    businessPlan = "",
                    techStack = listOf(),
                    resources = ""
                ),
                IdeaStatus.FINANCIAL_JUSTIFICATION_REQUIRED
            ),
            Arguments.of(
                IdeaRequest(
                    owner = 1,
                    problem = "text",
                    title = "text",
                    tgLink = "text",
                    description = "text",
                    scopes = listOf(1),
                    similarProjects = "text",
                    targetAudience = "text",
                    marketResearch = "text",
                    businessModels = listOf(1),
                    businessPlan = "text",
                    techStack = listOf(),
                    resources = ""
                ),
                IdeaStatus.SEARCH_TECHNOLOGICAL_SOLUTION
            ),
            Arguments.of(
                IdeaRequest(
                    owner = 1,
                    problem = "text",
                    title = "text",
                    tgLink = "text",
                    description = "text",
                    scopes = listOf(1),
                    similarProjects = "text",
                    targetAudience = "text",
                    marketResearch = "text",
                    businessModels = listOf(1),
                    businessPlan = "text",
                    techStack = listOf(1),
                    resources = ""
                ),
                IdeaStatus.SEARCH_TECHNOLOGICAL_SOLUTION
            ),
            Arguments.of(
                IdeaRequest(
                    owner = 1,
                    problem = "text",
                    title = "text",
                    tgLink = "text",
                    description = "text",
                    scopes = listOf(1),
                    similarProjects = "text",
                    targetAudience = "text",
                    marketResearch = "text",
                    businessModels = listOf(1),
                    businessPlan = "text",
                    techStack = listOf(1),
                    resources = "text"
                ),
                IdeaStatus.MVP_DEVELOPMENT
            ),
        )

    }

    @ParameterizedTest
    @MethodSource("updateData")
    fun `should change idea status when updated`(
        update: IdeaRequest,
        updatedStatus: IdeaStatus
    ) = with(testEngine) {
        val data = getFakeIdea()
        val created: IdeaResponse = postConverted(ADD_IDEA, data)
        val updated: IdeaResponse = putConverted(clientIdeaWithId(created.id), update)
        assertEquals(updatedStatus, updated.status)
    }

    @ParameterizedTest
    @MethodSource("createData")
    fun `should set status when idea created`(
        idea: IdeaRequest,
        ideaStatus: IdeaStatus
    ) = with(testEngine) {
        val created: IdeaResponse = postConverted(ADD_IDEA, idea)
        assertEquals(ideaStatus, created.status)
    }

    @ParameterizedTest
    @MethodSource("createData")
    fun `should change status to closed when stage is declined`(
        idea: IdeaRequest,
        ideaStatus: IdeaStatus
    ) = with(testEngine) {
        val id = postConverted<IdeaRequest, IdeaResponse>(ADD_IDEA, idea).id
        putOk(clientUpdateStage(id), IdeaStageRequest(IdeaStage.DECLINED))
        val response: IdeaResponse = getConverted(clientIdeaWithId(id))
        assertEquals(IdeaStatus.PROJECT_CLOSED, response.status)
    }

    @ParameterizedTest
    @MethodSource("createData")
    fun `should change status to closed when stage is done`(
        idea: IdeaRequest,
        ideaStatus: IdeaStatus
    ) = with(testEngine) {
        val id = postConverted<IdeaRequest, IdeaResponse>(ADD_IDEA, idea).id
        putOk(clientUpdateStage(id), IdeaStageRequest(IdeaStage.DONE))
        val response: IdeaResponse = getConverted(clientIdeaWithId(id))
        assertEquals(IdeaStatus.PROJECT_CLOSED, response.status)
    }
}