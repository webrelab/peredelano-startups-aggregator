package com.repedelano.services

import com.repedelano.dtos.Pager
import com.repedelano.dtos.idea.IdeaRequest
import com.repedelano.dtos.idea.IdeaResponse
import com.repedelano.dtos.idea.IdeaResponseList
import com.repedelano.dtos.idea.IdeaStage
import com.repedelano.dtos.technology.TechnologyRequest
import com.repedelano.extensions.page
import com.repedelano.flatMap
import com.repedelano.orm.helpers.toBusinessModel
import com.repedelano.orm.helpers.toIdea
import com.repedelano.orm.helpers.toIdeaResponseList
import com.repedelano.orm.helpers.toScope
import com.repedelano.orm.helpers.toTechnology
import com.repedelano.repositories.IdeaBusinessModelsRepository
import com.repedelano.repositories.IdeaRepository
import com.repedelano.repositories.IdeaScopesRepository
import com.repedelano.repositories.IdeaTechnologiesRepository
import com.repedelano.repositories.TechnologyRepository
import com.repedelano.repositories.VacancyRepository
import java.util.UUID

interface IdeaService {

    suspend fun insert(idea: IdeaRequest): Result<IdeaResponse?>
    suspend fun selectById(id: UUID): Result<IdeaResponse?>
    suspend fun selectPage(pager: Pager): Result<IdeaResponseList>
    suspend fun update(id: UUID, idea: IdeaRequest): Result<IdeaResponse?>
    suspend fun updateStatus(id: UUID, vacancies: Int): Result<Boolean>
    suspend fun updateStage(id: UUID, stage: IdeaStage): Result<IdeaResponse?>
}

class IdeaServiceImpl(
    private val ideaRepository: IdeaRepository,
    private val technologiesRepository: TechnologyRepository,
    private val ideaScopesRepository: IdeaScopesRepository,
    private val ideaTechnologiesRepository: IdeaTechnologiesRepository,
    private val ideaBusinessModelsRepository: IdeaBusinessModelsRepository,
    private val vacancyRepository: VacancyRepository,
) : IdeaService {

    override suspend fun insert(idea: IdeaRequest) =
        ideaRepository.insert(idea)
            .flatMap { id ->
                ideaRepository.update(id!!, IdeaStatusHandler.getStatus(idea))
                updateIdeaData(id, idea).map { id }
            }
            .flatMap { id ->
                selectById(id)
            }

    override suspend fun selectById(id: UUID) =
        ideaRepository.selectById(id)
            .map { row -> row!!.toIdea().also { collectIdeaData(it) } }

    override suspend fun selectPage(pager: Pager) =
        ideaRepository.selectAll()
            .flatMap { list ->
                val ideas = list.page(pager).map { it.toIdea() }
                var result = Result.success(Unit)
                for (idea in ideas) {
                    result = result.map { collectIdeaData(idea) }
                }
                result.map { ideas.toIdeaResponseList(pager.page, list.size) }
            }

    override suspend fun update(id: UUID, idea: IdeaRequest) =
        ideaRepository.update(id, idea)
            .flatMap { ideaScopesRepository.delete(id) }
            .flatMap { ideaBusinessModelsRepository.delete(id) }
            .flatMap { ideaTechnologiesRepository.delete(id) }
            .flatMap { updateIdeaData(id, idea) }
            .flatMap { vacancyRepository.selectByIdeaId(id).map { it.size } }
            .flatMap { updateStatus(id, it) }
            .flatMap { selectById(id) }

    override suspend fun updateStatus(id: UUID, vacancies: Int) =
        selectById(id).flatMap { ideaRepository.update(id, IdeaStatusHandler.getStatus(it, vacancies)) }

    override suspend fun updateStage(id: UUID, stage: IdeaStage) =
        ideaRepository.update(id, stage)
            .flatMap { vacancyRepository.selectByIdeaId(id).map { it.size } }
            .flatMap { updateStatus(id, it) }
            .flatMap { selectById(id) }

    private suspend fun collectIdeaData(idea: IdeaResponse) =
        Result.success(Unit)
            .flatMap { ideaScopesRepository.selectBuIdeaId(idea.id) }
            .map { list -> idea.scopes.addAll(list.map { it.toScope() }) }
            .flatMap { ideaBusinessModelsRepository.selectByIdeaId(idea.id) }
            .map { list -> idea.businessModels.addAll(list.map { it.toBusinessModel() }) }
            .flatMap { ideaTechnologiesRepository.selectByIdeaId(idea.id) }
            .map { list -> idea.techStack.addAll(list.map { it.toTechnology() }) }

    private suspend fun updateIdeaData(ideaId: UUID, idea: IdeaRequest): Result<Boolean> {
        idea.techStack?.forEach { technologiesRepository.insertIfNotExists(TechnologyRequest(it)) }
        var result = Result.success(false)
        idea.techStack?.forEach { stackName ->
            result = result.flatMap {
                ideaTechnologiesRepository.insert(ideaId, stackName)
            }
        }
        idea.scopes?.forEach { scopeName ->
            result = result.flatMap {
                ideaScopesRepository.insert(ideaId, scopeName)
            }
        }
        idea.businessModels?.forEach { bmName ->
            result = result.flatMap {
                ideaBusinessModelsRepository.insert(ideaId, bmName)
            }
        }
        return result
    }
}