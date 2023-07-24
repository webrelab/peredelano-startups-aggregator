package com.repedelano.usecases

import com.repedelano.dtos.technology.TechnologyRequest
import com.repedelano.dtos.technology.TechnologyResponse
import com.repedelano.dtos.technology.TechnologyResponseList
import com.repedelano.services.TechnologyService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

fun interface AddTechnologyUseCase {

    suspend fun add(technology: TechnologyRequest): Result<TechnologyResponse?>
}

fun interface GetTechnologyByIdUseCase {

    suspend fun get(id: Int): Result<TechnologyResponse?>
}

fun interface SearchTechnologyUseCase {

    suspend fun search(query: String): Result<TechnologyResponseList>
}

fun interface GetAllTechnologiesUseCase {

    suspend fun getAll(): Result<TechnologyResponseList>
}

fun interface UpdateTechnologyUseCase {

    suspend fun update(id: Int, technology: TechnologyRequest): Result<TechnologyResponse?>
}

fun addTechnologyUseCase(
    dispatcher: CoroutineDispatcher,
    technologyService: TechnologyService
) = AddTechnologyUseCase { technology ->
    withContext(dispatcher) {
        technologyService.insertIfNotExists(technology)
    }
}

fun getTechnologyByIdUseCase(
    dispatcher: CoroutineDispatcher,
    technologyService: TechnologyService
) = GetTechnologyByIdUseCase { id ->
    withContext(dispatcher) {
        technologyService.selectById(id)
    }
}

fun searchTechnologyUseCase(
    dispatcher: CoroutineDispatcher,
    technologyService: TechnologyService
) = SearchTechnologyUseCase { query ->
    withContext(dispatcher) {
        technologyService.search(query)
    }
}

fun getAllTechnologiesUseCase(
    dispatcher: CoroutineDispatcher,
    technologyService: TechnologyService
) = GetAllTechnologiesUseCase {
    withContext(dispatcher) {
        technologyService.selectAll()
    }
}

fun updateTechnologyUseCase(
    dispatcher: CoroutineDispatcher,
    technologyService: TechnologyService
) = UpdateTechnologyUseCase { id, technology ->
    withContext(dispatcher) {
        technologyService.update(id, technology)
    }
}