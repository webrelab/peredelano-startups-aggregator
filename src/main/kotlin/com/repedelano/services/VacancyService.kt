package com.repedelano.services

import com.repedelano.dtos.Pager
import com.repedelano.dtos.vacancy.VacancyRequest
import com.repedelano.dtos.vacancy.VacancyResponse
import com.repedelano.dtos.vacancy.VacancyResponseList
import com.repedelano.extensions.page
import com.repedelano.flatMap
import com.repedelano.orm.helpers.toProjectRoleResponse
import com.repedelano.orm.helpers.toVacancyResponse
import com.repedelano.orm.helpers.toVacancyResponseList
import com.repedelano.orm.vacancies.Vacancies
import com.repedelano.repositories.ProjectRoleRepository
import com.repedelano.repositories.VacancyRepository
import com.repedelano.repositories.VacancyTechnologiesRepository
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID

interface VacancyService {

    suspend fun insert(vacancy: VacancyRequest): Result<VacancyResponse>
    suspend fun selectById(id: Int): Result<VacancyResponse>
    suspend fun selectByIdeaId(id: UUID): Result<VacancyResponseList>
    suspend fun selectByTechStack(technologies: List<String>): Result<VacancyResponseList>
    suspend fun selectPage(pager: Pager): Result<VacancyResponseList>
    suspend fun update(id: Int, vacancy: VacancyRequest): Result<VacancyResponse>
}

class VacancyServiceImpl(
    private val vacancyRepository: VacancyRepository,
    private val vacancyTechnologiesRepository: VacancyTechnologiesRepository,
    private val projectRoleRepository: ProjectRoleRepository,
) : VacancyService {

    override suspend fun insert(vacancy: VacancyRequest) =
        vacancyRepository.insert(vacancy).flatMap { selectById(it!!) }

    override suspend fun selectById(id: Int) =
        vacancyRepository.selectById(id).flatMap { toVacancyResponseDto(it!!) }

    override suspend fun selectByIdeaId(id: UUID) =
        vacancyRepository.selectByIdeaId(id).flatMap { toVacancyResponseListDto(it) }

    override suspend fun selectByTechStack(technologies: List<String>) =
        vacancyRepository.selectByTechStack(technologies).flatMap { toVacancyResponseListDto(it) }

    override suspend fun selectPage(pager: Pager) =
        vacancyRepository.selectAll().map { it.page(pager) }.flatMap { toVacancyResponseListDto(it) }

    override suspend fun update(id: Int, vacancy: VacancyRequest) =
        vacancyRepository.update(id, vacancy).flatMap { selectById(id) }

    private suspend fun toVacancyResponseListDto(list: List<ResultRow>): Result<VacancyResponseList> {
        var result = Result.success(Unit)
        val dtoList = mutableListOf<VacancyResponse>()
        for (element in list) {
            result = result.flatMap { toVacancyResponseDto(element) }.map { dtoList.add(it) }
        }
        return result.map { dtoList }.map { it.toVacancyResponseList() }
    }

    private suspend fun toVacancyResponseDto(resultRow: ResultRow) =
        projectRoleRepository.selectById(resultRow[Vacancies.projectRole].value)
            .map { resultRow.toVacancyResponse(it!!.toProjectRoleResponse()) }
}