package com.repedelano.dtos

import kotlinx.serialization.Serializable

@Serializable
data class Pager(
    val page: Int = 0,
    val itemsPerPage: Int = 20
) {
    companion object {
        fun of(
            page: Int?,
            itemsPerPage: Int?
        ) = Pager(
            page ?: 0,
            itemsPerPage ?: 20
        )
    }
}
