package com.repedelano.orm.helpers

import com.repedelano.dtos.projectroles.ProjectRoleResponse
import com.repedelano.dtos.projectroles.ProjectRoleResponseList
import com.repedelano.orm.projrctroles.ProjectRoles
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toProjectRoleResponse(): ProjectRoleResponse {
    return with(ProjectRoles) {
        ProjectRoleResponse(
            id = get(id).value,
            name = get(name),
            description = get(description)
        )
    }
}

fun List<ProjectRoleResponse>.toProjectRoleResponseList() = ProjectRoleResponseList(this, size)