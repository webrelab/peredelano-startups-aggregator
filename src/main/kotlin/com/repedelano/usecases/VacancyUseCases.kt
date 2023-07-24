package com.repedelano.usecases

import com.repedelano.dtos.Pager
import com.repedelano.dtos.vacancy.VacancyRequest
import com.repedelano.dtos.vacancy.VacancyResponse
import com.repedelano.dtos.vacancy.VacancyResponseList
import com.repedelano.dtos.vacancy.VacancySearchQuery
import com.repedelano.dtos.vacancy.VacancyStatus
import com.repedelano.services.VacancyService
import io.ktor.server.sessions.directorySessionStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

fun interface AddVacancyUseCase {

    suspend fun add(vacancy: VacancyRequest): Result<VacancyResponse?>
}

fun interface GetVacancyByIdUseCase {

    suspend fun getById(id: Int): Result<VacancyResponse?>
}

fun interface SearchVacancyUseCase {

    suspend fun search(pager: Pager, query: VacancySearchQuery): Result<VacancyResponseList>
}

fun interface UpdateVacancyUseCase {

    suspend fun update(id: Int, vacancy: VacancyRequest): Result<VacancyResponse?>
}

fun interface UpdateVacancyStatusUseCase {

    suspend fun updateStatus(id: Int, status: VacancyStatus): Result<VacancyResponse?>
}

fun addVacancyUseCase(
    dispatcher: CoroutineDispatcher,
    vacancyService: VacancyService
) = AddVacancyUseCase { vacancy ->
    withContext(dispatcher) {
        vacancyService.insert(vacancy)
    }
}

fun getVacancyByIdUseCase(
    dispatcher: CoroutineDispatcher,
    vacancyService: VacancyService
) = GetVacancyByIdUseCase { id ->
    withContext(dispatcher) {
        vacancyService.selectById(id)
    }
}

fun searchVacancyUseCase(
    dispatcher: CoroutineDispatcher,
    vacancyService: VacancyService
) = SearchVacancyUseCase { pager, query ->
    withContext(dispatcher) {
        vacancyService.search(pager, query)
    }
}

fun updateVacancyUseCase(
    dispatcher: CoroutineDispatcher,
    vacancyService: VacancyService
) = UpdateVacancyUseCase { id, vacancy ->
    withContext(dispatcher) {
        vacancyService.update(id, vacancy)
    }
}

fun updateVacancyStatusUseCase(
    dispatcher: CoroutineDispatcher,
    vacancyService: VacancyService
) = UpdateVacancyStatusUseCase { id, status ->
    withContext(dispatcher) {
        vacancyService.updateStatus(id, status)
    }
}