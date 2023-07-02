package com.repedelano.dtos.businessmodel

import kotlinx.serialization.Serializable

@Serializable
data class BusinessModelRequest(
    val value: String,
    val description: String
)

@Serializable
data class BusinessModelResponse(
    val id: Int,
    val value: String,
    val description: String
)

@Serializable
data class BusinessModelResponseList(
    val businessModels: List<BusinessModelResponse>,
    val count: Int
)