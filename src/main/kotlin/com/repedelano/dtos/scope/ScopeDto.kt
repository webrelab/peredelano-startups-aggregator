package com.repedelano.dtos.scope

import kotlinx.serialization.Serializable

@Serializable
data class ScopeRequest(
    val value: String,
    val description: String,
)

@Serializable
data class ScopeResponse(
    val id: Int,
    val value: String,
    val description: String,
)

@Serializable
data class ScopeResponseList(
    val scopes: List<ScopeResponse>,
    val count: Int
)