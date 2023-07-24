package com.repedelano.usecases

import com.repedelano.dtos.Pager
import com.repedelano.dtos.idea.IdeaRequest
import com.repedelano.dtos.idea.IdeaResponse
import com.repedelano.dtos.idea.IdeaResponseList
import com.repedelano.dtos.idea.IdeaSearchRequest
import com.repedelano.dtos.idea.IdeaStage
import com.repedelano.services.IdeaService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.UUID

fun interface AddIdeaUseCase {

    suspend fun addIdea(ideaRequest: IdeaRequest): Result<IdeaResponse?>
}

fun interface GetIdeaUseCase {

    suspend fun getIdea(ideaId: UUID): Result<IdeaResponse?>
}

fun interface GetIdeasUseCase {

    suspend fun getPage(pager: Pager): Result<IdeaResponseList>
}

fun interface SearchIdeaUseCase {

    suspend fun search(pager: Pager, query: IdeaSearchRequest): Result<IdeaResponseList>
}

fun interface UpdateIdeaUseCase {

    suspend fun update(id: UUID, idea: IdeaRequest): Result<IdeaResponse?>
}

fun interface UpdateIdeaStageUseCase {

    suspend fun update(id: UUID, stage: IdeaStage): Result<IdeaResponse?>
}

fun addIdeaUseCase(
    dispatcher: CoroutineDispatcher,
    ideaService: IdeaService
) = AddIdeaUseCase { ideaRequest ->
    withContext(dispatcher) {
        ideaService.insert(ideaRequest)
    }
}

fun getIdeaUseCase(
    dispatcher: CoroutineDispatcher,
    ideaService: IdeaService
) = GetIdeaUseCase { ideaId ->
    withContext(dispatcher) {
        ideaService.selectById(ideaId)
    }
}

fun getIdeasUseCase(
    dispatcher: CoroutineDispatcher,
    ideaService: IdeaService
) = GetIdeasUseCase { pager ->
    withContext(dispatcher) {
        ideaService.selectPage(pager)
    }
}

fun searchIdeaUseCase(
    dispatcher: CoroutineDispatcher,
    ideaService: IdeaService
) = SearchIdeaUseCase{pager, query ->
    withContext(dispatcher) {
        ideaService.search(pager, query)
    }
}

fun updateIdeaUseCase(
    dispatcher: CoroutineDispatcher,
    ideaService: IdeaService
) = UpdateIdeaUseCase { id, idea ->
    withContext(dispatcher) {
        ideaService.update(id, idea)
    }
}

fun updateIdeaStageUseCase(
    dispatcher: CoroutineDispatcher,
    ideaService: IdeaService
) = UpdateIdeaStageUseCase { id, stage ->
    withContext(dispatcher) {
        ideaService.updateStage(id, stage)
    }
}