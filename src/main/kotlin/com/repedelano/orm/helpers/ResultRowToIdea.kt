package com.repedelano.orm.helpers

import com.repedelano.dtos.idea.IdeaResponse
import com.repedelano.dtos.idea.IdeaResponseList
import com.repedelano.orm.idea.Ideas
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toIdea(): IdeaResponse {
    return Ideas.run {
        IdeaResponse(
            id = get(id).value,
            created = get(created).toString(),
            updated = get(updated).toString(),
            owner = get(owner).value,
            title = get(title),
            tgLink = get(tgLink),
            scopes = mutableListOf(),
            isFavorite = get(isFavorite),
            problem = get(problem),
            description = get(description),
            businessModels = mutableListOf(),
            similarProjects = get(similarProjects),
            targetAudience = get(targetAudience),
            marketResearch = get(marketResearch),
            businessPlan = get(businessPlan),
            techStack = mutableListOf(),
            resources = get(resources),
            status = get(status),
            stage = get(stage)
        )
    }
}

fun List<IdeaResponse>.toIdeaResponseList(
    page: Int,
    total: Int
): IdeaResponseList {
    return IdeaResponseList(
        this,
        this.size,
        page,
        total
    )
}