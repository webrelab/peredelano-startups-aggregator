package com.repedelano.dtos.technology

import kotlinx.serialization.Serializable

@Serializable
data class TechnologyRequest(
    val value: String
)

@Serializable
data class TechnologyResponse(
    val id: Int,
    val value: String
)

@Serializable
data class TechnologyResponseList(
    val technologies: List<TechnologyResponse>,
    val count: Int
)