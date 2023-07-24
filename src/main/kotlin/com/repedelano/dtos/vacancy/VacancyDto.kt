package com.repedelano.dtos.vacancy

import com.repedelano.dtos.UUIDSerializer
import com.repedelano.dtos.projectroles.ProjectRoleResponse
import com.repedelano.dtos.technology.TechnologyResponse
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class VacancyRequest(
    @Serializable(with = UUIDSerializer::class)
    val ideaId: UUID,
    val projectRoleId: Int,
    val techStack: List<Int>,
    val description: String,
)

@Serializable
data class VacancyResponse(
    val id: Int,
    @Serializable(with = UUIDSerializer::class)
    val ideaId: UUID,
    val projectRole: ProjectRoleResponse,
    val status: VacancyStatus,
    val techStack: MutableList<TechnologyResponse>,
    val description: String,
)

@Serializable
data class VacancySearchQuery(
    @Serializable(with = UUIDSerializer::class)
    val ideaId: UUID? = null,
    val projectRole: Int? = null,
    val techStack: List<Int>,
    val description: String? = null,
    val status: VacancyStatus? = null,
)

@Serializable
data class VacancyResponseList(
    val vacancies: List<VacancyResponse>,
    val count: Int,
    val page: Int,
    val total: Int,
)

@Serializable
data class VacancyRequestStatus(
    val status: VacancyStatus
)