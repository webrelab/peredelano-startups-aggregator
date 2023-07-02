package com.repedelano.services

import com.repedelano.dtos.technology.TechnologyRequest
import com.repedelano.dtos.technology.TechnologyResponse
import com.repedelano.dtos.technology.TechnologyResponseList
import com.repedelano.flatMap
import com.repedelano.orm.helpers.toTechnology
import com.repedelano.orm.helpers.toTechnologyResponseList
import com.repedelano.repositories.TechnologyRepository

interface TechnologyService {

    suspend fun insertIfNotExists(technology: TechnologyRequest): Result<TechnologyResponse>
    suspend fun selectById(id: Int): Result<TechnologyResponse>
    suspend fun selectByName(name: String): Result<TechnologyResponse>
    suspend fun selectAll(): Result<TechnologyResponseList>
}

class TechnologyServiceImpl(
    private val technologyRepository: TechnologyRepository
) : TechnologyService {

    override suspend fun insertIfNotExists(technology: TechnologyRequest) =
        technologyRepository.insertIfNotExists(technology).flatMap { selectById(it!!) }

    override suspend fun selectById(id: Int) =
        technologyRepository.selectById(id).map { it!!.toTechnology() }

    override suspend fun selectByName(name: String) =
        technologyRepository.selectByName(name).map { it!!.toTechnology() }

    override suspend fun selectAll() =
        technologyRepository.selectAll().map { list ->
            list.map { it.toTechnology() }.toTechnologyResponseList()
        }
}