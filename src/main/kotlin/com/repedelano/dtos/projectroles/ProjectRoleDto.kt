package com.repedelano.dtos.projectroles

import kotlinx.serialization.Serializable

@Serializable
data class ProjectRoleRequest(
    val name: String,
    val description: String?,
)

@Serializable
data class ProjectRoleResponse(
    val id: Int,
    val name: String,
    val description: String?,
)

@Serializable
data class ProjectRoleResponseList(
    val projectRoles: List<ProjectRoleResponse>,
    val count: Int,
)