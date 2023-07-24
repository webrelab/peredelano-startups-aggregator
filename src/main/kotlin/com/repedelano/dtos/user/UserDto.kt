package com.repedelano.dtos.user

import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(
    val passportId: String,
    val name: String,
    val lastName: String,
    val email: String,
    val tgUser: String,
    val picture: String,
)

@Serializable
data class UserSearchRequest(
    val passportId: String? = null,
    val name: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val tgUser: String? = null,
)

@Serializable
data class UserResponse(
    val id: Int,
    val passportId: String,
    val name: String,
    val lastName: String,
    val email: String,
    val tgUser: String,
    val picture: String,
    val registered: String,
)

@Serializable
data class UserResponseList(
    val users: List<UserResponse>,
    val count: Int,
    val page: Int,
    val total: Int,
)