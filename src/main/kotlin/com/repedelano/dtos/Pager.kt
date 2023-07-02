package com.repedelano.dtos

import kotlinx.serialization.Serializable

@Serializable
data class Pager(
    val page: Int,
    val itemsPerPage: Int = 20
)
