package com.repedelano.services

import com.repedelano.dtos.technology.TechnologyRequest
import com.repedelano.dtos.technology.TechnologyResponse
import com.repedelano.dtos.technology.TechnologyResponseList
import com.repedelano.flatMap
import com.repedelano.orm.helpers.toTechnology
import com.repedelano.orm.helpers.toTechnologyResponseList
import com.repedelano.repositories.TechnologyRepository

interface TechnologyService {

    suspend fun insertIfNotExists(technology: TechnologyRequest): Result<TechnologyResponse?>
    suspend fun selectById(id: Int): Result<TechnologyResponse?>
    suspend fun search(query: String): Result<TechnologyResponseList>
    suspend fun selectAll(): Result<TechnologyResponseList>
    suspend fun update(id: Int, technology: TechnologyRequest): Result<TechnologyResponse?>
}

class TechnologyServiceImpl(
    private val technologyRepository: TechnologyRepository
) : TechnologyService {

    override suspend fun insertIfNotExists(technology: TechnologyRequest) =
        technologyRepository.insertIfNotExists(technology).flatMap { selectById(it!!) }

    override suspend fun selectById(id: Int) =
        technologyRepository.selectById(id).map { it!!.toTechnology() }

    override suspend fun search(query: String) =
        technologyRepository.search(query).map { list->
            list.map { it.toTechnology() }.toTechnologyResponseList()
        }

    override suspend fun selectAll() =
        technologyRepository.selectAll().map { list ->
            list.map { it.toTechnology() }.toTechnologyResponseList()
        }

    override suspend fun update(id: Int, technology: TechnologyRequest) =
        technologyRepository.update(id, technology)
            .flatMap { selectById(id) }
}