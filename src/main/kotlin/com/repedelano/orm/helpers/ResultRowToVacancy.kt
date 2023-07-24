package com.repedelano.orm.helpers

import com.repedelano.dtos.projectroles.ProjectRoleResponse
import com.repedelano.dtos.vacancy.VacancyResponse
import com.repedelano.dtos.vacancy.VacancyResponseList
import com.repedelano.orm.vacancies.Vacancies
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toVacancyResponse(
    projectRole: ProjectRoleResponse
): VacancyResponse {

    return with(Vacancies) {
        VacancyResponse(
            id = get(id).value,
            ideaId = get(ideaId).value,
            projectRole = projectRole,
            status = get(status),
            techStack = mutableListOf(),
            description = get(description)
        )
    }
}

fun List<VacancyResponse>.toVacancyResponseList(
    page: Int = 0,
    total: Int = size
): VacancyResponseList {
   return VacancyResponseList(this, size, page, total)
}