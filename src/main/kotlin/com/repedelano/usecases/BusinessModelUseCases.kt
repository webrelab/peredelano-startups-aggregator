package com.repedelano.usecases

import com.repedelano.dtos.businessmodel.BusinessModelRequest
import com.repedelano.dtos.businessmodel.BusinessModelResponse
import com.repedelano.dtos.businessmodel.BusinessModelResponseList
import com.repedelano.services.BusinessModelService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

fun interface AddBusinessModelUseCase {

    suspend fun add(businessModel: BusinessModelRequest): Result<BusinessModelResponse?>
}

fun interface GetBusinessModelByIdUseCase {

    suspend fun get(id: Int): Result<BusinessModelResponse?>
}

fun interface GetBusinessModelsUseCase {

    suspend fun getAll(): Result<BusinessModelResponseList>
}

fun interface UpdateBusinessModelUseCase {

    suspend fun update(id: Int, businessModel: BusinessModelRequest): Result<BusinessModelResponse?>
}

fun addBusinessModelUseCase(
    dispatcher: CoroutineDispatcher,
    businessModelService: BusinessModelService
) = AddBusinessModelUseCase { businessModel ->
    withContext(dispatcher) {
        businessModelService.insert(businessModel)
    }
}

fun getBusinessModelByIdUseCase(
    dispatcher: CoroutineDispatcher,
    businessModelService: BusinessModelService
) = GetBusinessModelByIdUseCase { id ->
    withContext(dispatcher) {
        businessModelService.selectById(id)
    }
}

fun getBusinessModelsUseCase(
    dispatcher: CoroutineDispatcher,
    businessModelService: BusinessModelService
) = GetBusinessModelsUseCase {
    withContext(dispatcher) {
        businessModelService.selectAll()
    }
}

fun updateBusinessModelUseCase(
    dispatcher: CoroutineDispatcher,
    businessModelService: BusinessModelService
) = UpdateBusinessModelUseCase { id, businessModel ->
    withContext(dispatcher) {
        businessModelService.update(id, businessModel)
    }
}