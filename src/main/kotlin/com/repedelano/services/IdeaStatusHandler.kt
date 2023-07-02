@file:Suppress("DuplicatedCode")

package com.repedelano.services

import com.repedelano.dtos.idea.IdeaRequest
import com.repedelano.dtos.idea.IdeaResponse
import com.repedelano.dtos.idea.IdeaStage
import com.repedelano.dtos.idea.IdeaStatus

object IdeaStatusHandler {

    fun getStatus(ideaRequest: IdeaRequest, vacanciesTotal: Int = 0, stage: IdeaStage = IdeaStage.OPEN): IdeaStatus {
        return ideaRequest.run {
            when {
                stage == IdeaStage.DONE || stage == IdeaStage.DECLINED -> IdeaStatus.PROJECT_CLOSED
                isBlank(description) -> IdeaStatus.SEARCH_SOLUTION
                isBlank(scopes) || isBlank(similarProjects, targetAudience, marketResearch) -> IdeaStatus.COLLECT_SOCIO_ECONOMIC_INFO
                isBlank(businessModel) || isBlank(businessPlan) -> IdeaStatus.FINANCIAL_JUSTIFICATION_REQUIRED
                isBlank(techStack) || isBlank(resources) -> IdeaStatus.SEARCH_TECHNOLOGICAL_SOLUTION
                vacanciesTotal > 0 -> IdeaStatus.SEARCH_PARTICIPANTS
                else -> IdeaStatus.MVP_DEVELOPMENT
            }
        }
    }

    fun getStatus(ideaResponse: IdeaResponse, vacanciesTotal: Int): IdeaStatus {
        return ideaResponse.run {
            when {
                stage == IdeaStage.DONE || stage == IdeaStage.DECLINED -> IdeaStatus.PROJECT_CLOSED
                isBlank(description) -> IdeaStatus.SEARCH_SOLUTION
                isBlank(scopes) || isBlank(similarProjects, targetAudience, marketResearch) -> IdeaStatus.COLLECT_SOCIO_ECONOMIC_INFO
                isBlank(businessModel) || isBlank(businessPlan) -> IdeaStatus.FINANCIAL_JUSTIFICATION_REQUIRED
                isBlank(techStack) || isBlank(resources) -> IdeaStatus.SEARCH_TECHNOLOGICAL_SOLUTION
                vacanciesTotal > 0 -> IdeaStatus.SEARCH_PARTICIPANTS
                else -> IdeaStatus.MVP_DEVELOPMENT
            }
        }
    }

    private fun isBlank(vararg fields: String?) = fields.any { it == null || it.isBlank() }

    private fun isBlank(vararg listFields: List<*>?) = listFields.any { it.isNullOrEmpty() }
}