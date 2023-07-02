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

    suspend fun add(idea: IdeaRequest): Result<IdeaResponse?>
    suspend fun selectById(id: UUID): Result<IdeaResponse?>
    suspend fun selectPage(pager: Pager): Result<IdeaResponseList>
    suspend fun update(id: UUID, idea: IdeaRequest): Result<Boolean>
    suspend fun updateStage(id: UUID, stage: IdeaStage): Result<Boolean>
}

class IdeaServiceImpl(
    private val ideaRepository: IdeaRepository,
    private val technologiesRepository: TechnologyRepository,
    private val ideaScopesRepository: IdeaScopesRepository,
    private val ideaTechnologiesRepository: IdeaTechnologiesRepository,
    private val ideaBusinessModelsRepository: IdeaBusinessModelsRepository,
    private val vacancyRepository: VacancyRepository,
) : IdeaService {

    override suspend fun add(idea: IdeaRequest): Result<IdeaResponse?> {
        return ideaRepository.add(idea)
            .flatMap { id ->
                ideaRepository.update(id!!, IdeaStatusHandler.getStatus(idea))
                updateIdeaData(id, idea).map { id }
            }
            .flatMap { id ->
                selectById(id)
            }
    }

    override suspend fun selectById(id: UUID): Result<IdeaResponse?> {
        return ideaRepository.selectById(id).map { row ->
            val idea = row!!.toIdea()
            collectIdeaData(idea)
            idea
        }
    }

    override suspend fun selectPage(pager: Pager): Result<IdeaResponseList> {
        return ideaRepository.selectAll().flatMap { list ->
            val ideas = list.page(pager).map { it.toIdea() }
            var result = Result.success(Unit)
            for (idea in ideas) {
                result = result.map { collectIdeaData(idea) }
            }
            result.map { ideas.toIdeaResponseList(pager.page, list.size) }
        }
    }

    override suspend fun update(id: UUID, idea: IdeaRequest): Result<Boolean> {
        return ideaRepository.update(id, idea)
            .flatMap { ideaScopesRepository.delete(id) }
            .flatMap { ideaBusinessModelsRepository.delete(id) }
            .flatMap { ideaTechnologiesRepository.delete(id) }
            .flatMap { updateIdeaData(id, idea) }
    }

    override suspend fun updateStage(id: UUID, stage: IdeaStage): Result<Boolean> {
        return ideaRepository.update(id, stage)
    }

    private suspend fun collectIdeaData(idea: IdeaResponse): Result<Unit> {
        return Result.success(Unit)
            .flatMap { ideaScopesRepository.selectBuIdeaId(idea.id) }
            .map { list -> idea.scopes.addAll(list.map { it.toScope() }) }
            .flatMap { ideaBusinessModelsRepository.selectByIdeaId(idea.id) }
            .map { list -> idea.businessModel.addAll(list.map { it.toBusinessModel() }) }
            .flatMap { ideaTechnologiesRepository.selectByIdeaId(idea.id) }
            .map { list -> idea.techStack.addAll(list.map { it.toTechnology() }) }
    }

    private suspend fun updateIdeaData(ideaId: UUID, idea: IdeaRequest): Result<Boolean> {
        idea.techStack?.forEach { technologiesRepository.addIfNotExists(TechnologyRequest(it)) }
        var result = Result.success(false)
        idea.techStack?.forEach { stackName ->
            result = result.flatMap {
                ideaTechnologiesRepository.add(ideaId, stackName)
            }
        }
        idea.scopes?.forEach { scopeName ->
            result = result.flatMap {
                ideaScopesRepository.add(ideaId, scopeName)
            }
        }
        idea.businessModel?.forEach { bmName ->
            result = result.flatMap {
                ideaBusinessModelsRepository.add(ideaId, bmName)
            }
        }
        return result
    }
}