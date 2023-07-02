package com.repedelano.usecases

import com.repedelano.dtos.idea.IdeaRequest
import com.repedelano.dtos.idea.IdeaResponse
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