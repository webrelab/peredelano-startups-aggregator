package com.repedelano.dtos.vacancy

import com.repedelano.dtos.projectroles.ProjectRoleResponse
import com.repedelano.dtos.technology.TechnologyResponse
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class VacancyRequest(
    @Contextual
    val ideaId: UUID,
    val projectRole: String,
    val techStack: List<String>,
    val description: String,
)

@Serializable
data class VacancyResponse(
    val id: Int,
    @Contextual
    val ideaId: UUID,
    val projectRole: ProjectRoleResponse,
    val status: VacancyStatus,
    val techStack: MutableList<TechnologyResponse>,
    val description: String,
)

@Serializable
data class VacancyResponseList(
    val vacancies: List<VacancyResponse>,
    val count: Int,
    val page: Int,
    val total: Int,
)