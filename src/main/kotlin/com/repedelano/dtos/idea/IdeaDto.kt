package com.repedelano.dtos.idea

import com.repedelano.dtos.UUIDSerializer
import com.repedelano.dtos.businessmodel.BusinessModelResponse
import com.repedelano.dtos.scope.ScopeResponse
import com.repedelano.dtos.technology.TechnologyResponse
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class IdeaRequest(
    val owner: Int? = null,
    val title: String? = null,
    val tgLink: String? = null,
    val scopes: List<Int>? = null,
    val problem: String? = null,
    val description: String? = null,
    val businessModels: List<Int>? = null,
    val similarProjects: String? = null,
    val targetAudience: String? = null,
    val marketResearch: String? = null,
    val businessPlan: String? = null,
    val techStack: List<Int>? = null,
    val resources: String? = null,
)

@Serializable
data class IdeaSearchRequest(
    val owner: Int?,
    val queryString: String?,
    val scopes: List<Int>,
    val businessModels: List<Int>,
    val techStack: List<Int>,
)

@Serializable
data class IdeaResponse(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val created: String,
    val updated: String,
    val owner: Int?,
    val title: String?,
    val tgLink: String?,
    val scopes: MutableList<ScopeResponse>,
    val isFavorite: Boolean,
    val problem: String?,
    val description: String?,
    val businessModels: MutableList<BusinessModelResponse>,
    val similarProjects: String?,
    val targetAudience: String?,
    val marketResearch: String?,
    val businessPlan: String?,
    val techStack: MutableList<TechnologyResponse>,
    val resources: String?,
    val status: IdeaStatus,
    val stage: IdeaStage,
)

@Serializable
data class IdeaStageRequest(
    val stage: IdeaStage
)

@Serializable
data class IdeaResponseList(
    val ideas: List<IdeaResponse>,
    val count: Int,
    val page: Int,
    val total: Int,
)