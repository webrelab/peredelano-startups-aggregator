package com.peredelano.routing

import com.peredelano.BaseTest
import com.peredelano.ext.assertAll
import com.peredelano.ext.getConverted
import com.peredelano.ext.postConverted
import com.peredelano.ext.putOk
import com.repedelano.datagenerator.RequestGenerators
import com.repedelano.datagenerator.RequestGenerators.getFakeIdea
import com.repedelano.dtos.idea.IdeaRequest
import com.repedelano.dtos.idea.IdeaResponse
import com.repedelano.dtos.idea.IdeaResponseList
import com.repedelano.dtos.idea.IdeaStage
import com.repedelano.dtos.idea.IdeaStageRequest
import com.repedelano.routes.IdeaRoutes.Companion.ADD_IDEA
import com.repedelano.routes.IdeaRoutes.Companion.IDEAS
import com.repedelano.routes.IdeaRoutes.Companion.clientGetPage
import com.repedelano.routes.IdeaRoutes.Companion.clientIdeaWithId
import com.repedelano.routes.IdeaRoutes.Companion.clientUpdateStage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class IdeasTest : BaseTest() {

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
    }

    @Test
    fun addIdea() = with(testEngine) {
        repeat(200) {
            val data = getFakeIdea()
            val response: IdeaResponse = postConverted(ADD_IDEA, data)
            response.assertAll(data)
        }
    }

    @Test
    fun getById() = with(testEngine) {
        repeat(200) {
            val data = getFakeIdea()
            val createdId = postConverted<IdeaRequest, IdeaResponse>(ADD_IDEA, data).id
            val idea: IdeaResponse = getConverted(clientIdeaWithId(createdId))
            idea.assertAll(data)
        }
    }

    @Test
    fun `should load only 20 ideas`() = with(testEngine) {
        val response: IdeaResponseList = getConverted(IDEAS)
        assertEquals(20, response.count)
    }

    @Test
    fun `check last page`() = with(testEngine) {
        val total = getConverted<IdeaResponseList>(IDEAS).total
        val lastPage: IdeaResponseList = getConverted(clientGetPage(total / 20, 20))
        assertEquals(total % 20, lastPage.count)
    }

    @Test
    fun getAll() = with(testEngine) {
        val responseList: IdeaResponseList = getConverted(IDEAS)
        assertTrue(responseList.total >= 30)
    }

    @Test
    fun updateStageToDeclined() = with(testEngine) {
        val responseList: IdeaResponseList = getConverted(IDEAS)
        val id = responseList.ideas[0].id
        putOk(clientUpdateStage(id), IdeaStageRequest(IdeaStage.DECLINED))
        val response: IdeaResponse = getConverted(clientIdeaWithId(id))
        assertEquals(IdeaStage.DECLINED, response.stage)
    }

    @Test
    fun updateStageToDone() = with(testEngine) {
        val responseList: IdeaResponseList = getConverted(IDEAS)
        val id = responseList.ideas[1].id
        putOk(clientUpdateStage(id), IdeaStageRequest(IdeaStage.DONE))
        val response: IdeaResponse = getConverted(clientIdeaWithId(id))
        assertEquals(IdeaStage.DONE, response.stage)
    }
}