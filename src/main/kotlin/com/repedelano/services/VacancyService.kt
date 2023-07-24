package com.repedelano.services

import com.repedelano.dtos.Pager
import com.repedelano.dtos.projectroles.ProjectRoleRequest
import com.repedelano.dtos.technology.TechnologyRequest
import com.repedelano.dtos.vacancy.VacancyRequest
import com.repedelano.dtos.vacancy.VacancyResponse
import com.repedelano.dtos.vacancy.VacancyResponseList
import com.repedelano.dtos.vacancy.VacancySearchQuery
import com.repedelano.dtos.vacancy.VacancyStatus
import com.repedelano.extensions.page
import com.repedelano.flatMap
import com.repedelano.orm.helpers.toProjectRoleResponse
import com.repedelano.orm.helpers.toTechnology
import com.repedelano.orm.helpers.toTechnologyResponseList
import com.repedelano.orm.helpers.toVacancyResponse
import com.repedelano.orm.helpers.toVacancyResponseList
import com.repedelano.orm.vacancies.Vacancies
import com.repedelano.repositories.ProjectRoleRepository
import com.repedelano.repositories.TechnologyRepository
import com.repedelano.repositories.VacancyRepository
import com.repedelano.repositories.VacancyTechnologiesRepository
import com.repedelano.resultOf
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID

interface VacancyService {

    suspend fun insert(vacancy: VacancyRequest): Result<VacancyResponse?>
    suspend fun selectById(id: Int): Result<VacancyResponse?>
    suspend fun search(pager: Pager, query: VacancySearchQuery): Result<VacancyResponseList>
    suspend fun update(id: Int, vacancy: VacancyRequest): Result<VacancyResponse?>
    suspend fun updateStatus(id: Int, status: VacancyStatus): Result<VacancyResponse?>
}

class VacancyServiceImpl(
    private val vacancyRepository: VacancyRepository,
    private val vacancyTechnologiesRepository: VacancyTechnologiesRepository,
    private val projectRoleRepository: ProjectRoleRepository,
    private val ideaService: IdeaService,
) : VacancyService {

    override suspend fun insert(vacancy: VacancyRequest) =
        vacancyRepository.insert(vacancy).flatMap { id ->
            id?.let { notNullId ->
                updateTechStack(notNullId, vacancy.techStack)
                    .flatMap { ideaService.updateStatus(vacancy.ideaId, 1) }
                    .flatMap { selectById(notNullId) }
            } ?: Result.failure(IllegalStateException("Insert operation failed, id is null"))
        }

    override suspend fun selectById(id: Int): Result<VacancyResponse?> =
        vacancyRepository.selectById(id)
            .flatMap {
                it?.let { notNullRow ->
                    toVacancyResponseDto(notNullRow)
                } ?: Result.failure(IllegalStateException("Select operation failed"))
            }

    override suspend fun search(pager: Pager, query: VacancySearchQuery): Result<VacancyResponseList> {
        return vacancyRepository.search(query).map { it.page(pager) }
            .flatMap { toVacancyResponseListDto(it) }
    }

    override suspend fun update(id: Int, vacancy: VacancyRequest) =
        vacancyRepository.update(id = id, vacancy)
            .flatMap { updateTechStack(id, vacancy.techStack) }
            .flatMap { ideaService.updateStatus(vacancy.ideaId, 1) }
            .flatMap { selectById(id) }

    override suspend fun updateStatus(id: Int, status: VacancyStatus) =
        vacancyRepository.updateStatus(id, status)
            .flatMap { selectById(id) }
            .flatMap {
                it?.let { vacancy ->
                    vacancyRepository.search(
                        VacancySearchQuery(
                            ideaId = vacancy.ideaId,
                            status = VacancyStatus.OPEN,
                            techStack = emptyList()
                        )
                    ).flatMap { searchResult ->
                        ideaService.updateStatus(vacancy.ideaId, searchResult.size)
                    }.map { vacancy }
                } ?: Result.failure(IllegalStateException("Select operation failed"))
            }

    private suspend fun toVacancyResponseListDto(list: List<ResultRow>): Result<VacancyResponseList> {
        var result = Result.success(Unit)
        val dtoList = mutableListOf<VacancyResponse>()
        for (element in list) {
            result = result.flatMap {
                toVacancyResponseDto(element)
            }.map { it?.let { dtoList.add(it) } }
        }
        return result.map { dtoList }.map { it.toVacancyResponseList() }
    }

    private suspend fun toVacancyResponseDto(resultRow: ResultRow): Result<VacancyResponse?> =
        projectRoleRepository.selectById(resultRow[Vacancies.projectRoleId].value)
            .flatMap {
                it?.let { notNullResultRow ->
                    resultOf { resultRow.toVacancyResponse(notNullResultRow.toProjectRoleResponse()) }
                        .flatMap { vacancyResponse ->
                            collectTechStack(vacancyResponse)
                        }
                } ?: Result.failure(IllegalStateException("Select operation failed"))
            }

    private suspend fun updateTechStack(vacancyId: Int, techStack: List<Int>?) =
        techStack?.let { notNullTechStack ->
            vacancyTechnologiesRepository.selectByVacancyId(vacancyId).map { list ->
                list.map { it.toTechnology().id }.toSet()
            }.flatMap { currentTechStack ->
                val toAdd = notNullTechStack.toSet() - currentTechStack
                val toRemove = currentTechStack - notNullTechStack.toSet()
                vacancyTechnologiesRepository.insert(vacancyId, toAdd)
                    .flatMap {
                        vacancyTechnologiesRepository.delete(vacancyId, toRemove)
                    }
            }
        } ?: Result.success(false)

    private suspend fun collectTechStack(vacancy: VacancyResponse) =
        vacancyTechnologiesRepository.selectByVacancyId(vacancy.id)
            .map { list -> list.map { it.toTechnology() }.toTechnologyResponseList() }
            .map { vacancy.techStack.addAll(it.technologies) }
            .map { vacancy }
}