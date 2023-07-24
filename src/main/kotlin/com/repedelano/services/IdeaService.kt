package com.repedelano.services

import com.repedelano.dtos.Pager
import com.repedelano.dtos.idea.IdeaRequest
import com.repedelano.dtos.idea.IdeaResponse
import com.repedelano.dtos.idea.IdeaResponseList
import com.repedelano.dtos.idea.IdeaSearchRequest
import com.repedelano.dtos.idea.IdeaStage
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
import com.repedelano.repositories.VacancyRepository
import java.util.UUID

interface IdeaService {

    suspend fun insert(idea: IdeaRequest): Result<IdeaResponse?>
    suspend fun selectById(id: UUID): Result<IdeaResponse?>
    suspend fun selectPage(pager: Pager): Result<IdeaResponseList>
    suspend fun search(pager: Pager, query: IdeaSearchRequest): Result<IdeaResponseList>
    suspend fun update(id: UUID, idea: IdeaRequest): Result<IdeaResponse?>
    suspend fun updateStatus(id: UUID, vacancies: Int): Result<Boolean>
    suspend fun updateStage(id: UUID, stage: IdeaStage): Result<IdeaResponse?>
}

class IdeaServiceImpl(
    private val ideaRepository: IdeaRepository,
    private val ideaScopesRepository: IdeaScopesRepository,
    private val ideaTechnologiesRepository: IdeaTechnologiesRepository,
    private val ideaBusinessModelsRepository: IdeaBusinessModelsRepository,
    private val vacancyRepository: VacancyRepository,
) : IdeaService {

    override suspend fun insert(idea: IdeaRequest) =
        ideaRepository.insert(idea)
            .flatMap { id ->
                id?.let { nonNullId ->
                    updateTechStack(id, idea.techStack)
                        .flatMap { updateScopes(id, idea.scopes) }
                        .flatMap { updateBusinessModels(id, idea.businessModels) }
                        .flatMap { ideaRepository.update(nonNullId, IdeaStatusHandler.getStatus(idea)) }
                        .flatMap { selectById(id) }
                } ?: Result.failure(IllegalStateException("Insert operation failed, id is null"))
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

    override suspend fun search(pager: Pager, query: IdeaSearchRequest) =
        ideaRepository.search(
            owner = query.owner,
            queryString = query.queryString,
            scopes = query.scopes,
            businessModels = query.businessModels,
            technologies = query.techStack
        ).map { list ->
            list.map { resultRow -> resultRow.toIdea().also { collectIdeaData(it) } }
                .page(pager).toIdeaResponseList(pager.page, list.size)
        }

    override suspend fun update(id: UUID, idea: IdeaRequest) =
        ideaRepository.update(id, idea)
            .flatMap { updateTechStack(id, idea.techStack) }
            .flatMap { updateScopes(id, idea.scopes) }
            .flatMap { updateBusinessModels(id, idea.businessModels) }
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
            .flatMap { ideaScopesRepository.selectByIdeaId(idea.id) }
            .map { list -> idea.scopes.addAll(list.map { it.toScope() }) }
            .flatMap { ideaBusinessModelsRepository.selectByIdeaId(idea.id) }
            .map { list -> idea.businessModels.addAll(list.map { it.toBusinessModel() }) }
            .flatMap { ideaTechnologiesRepository.selectByIdeaId(idea.id) }
            .map { list -> idea.techStack.addAll(list.map { it.toTechnology() }) }

    private suspend fun updateIdeaData(ideaId: UUID, idea: IdeaRequest) =
        updateTechStack(ideaId, idea.techStack)
            .flatMap { updateScopes(ideaId, idea.scopes) }
            .flatMap { updateBusinessModels(ideaId, idea.businessModels) }

    private suspend fun updateTechStack(ideaId: UUID, techStack: List<Int>?) =
        techStack?.let {
            ideaTechnologiesRepository.selectByIdeaId(ideaId).map { list ->
                list.map { it.toTechnology().id }.toSet()
            }.flatMap { currentTechStack ->
                val toAdd = techStack.toSet() - currentTechStack
                val toRemove = currentTechStack - techStack.toSet()
                ideaTechnologiesRepository.insert(ideaId, toAdd)
                    .flatMap {
                        ideaTechnologiesRepository.delete(ideaId, toRemove)
                    }
            }
        } ?: Result.success(false)

    private suspend fun updateScopes(ideaId: UUID, scopes: List<Int>?) =
        scopes?.let {
            ideaScopesRepository.selectByIdeaId(ideaId).map { list ->
                list.map { it.toScope().id }.toSet()
            }.flatMap { currentScopes ->
                val toAdd = scopes.toSet() - currentScopes
                val toRemove = currentScopes - scopes.toSet()
                ideaScopesRepository.insert(ideaId, toAdd)
                    .flatMap {
                        ideaScopesRepository.delete(ideaId, toRemove)
                    }
            }
        } ?: Result.success(false)

    private suspend fun updateBusinessModels(ideaId: UUID, businessModelIds: List<Int>?) =
        businessModelIds?.let {
            ideaBusinessModelsRepository.selectByIdeaId(ideaId).map { list ->
                list.map { it.toBusinessModel().id }.toSet()
            }.flatMap { currentBusinessModelIds ->
                val toAdd = businessModelIds.toSet() - currentBusinessModelIds
                val toRemove = currentBusinessModelIds - businessModelIds.toSet()
                ideaBusinessModelsRepository.insert(ideaId, toAdd)
                    .flatMap {
                        ideaBusinessModelsRepository.delete(ideaId, toRemove)
                    }
            }
        } ?: Result.success(false)
}