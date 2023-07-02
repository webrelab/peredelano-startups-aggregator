package com.repedelano.utils.db

data class DbConfig(
    val driver: String,
    val url: String,
    val username: String,
    val password: String,
    val maxPoolSize: Int,
)