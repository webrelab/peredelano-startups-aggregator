package com.repedelano.services

import com.repedelano.dtos.businessmodel.BusinessModelRequest
import com.repedelano.dtos.businessmodel.BusinessModelResponse
import com.repedelano.dtos.businessmodel.BusinessModelResponseList
import com.repedelano.flatMap
import com.repedelano.orm.helpers.toBusinessModel
import com.repedelano.orm.helpers.toBusinessModelList
import com.repedelano.repositories.BusinessModelRepository

interface BusinessModelService {

    suspend fun insert(businessModel: BusinessModelRequest): Result<BusinessModelResponse?>
    suspend fun selectById(id: Int): Result<BusinessModelResponse?>
    suspend fun selectAll(): Result<BusinessModelResponseList>
    suspend fun update(id: Int, businessModel: BusinessModelRequest): Result<BusinessModelResponse?>
}

class BusinessModelServiceImpl(
    private val businessModelRepository: BusinessModelRepository
) : BusinessModelService {

    override suspend fun insert(businessModel: BusinessModelRequest) =
        businessModelRepository.insert(businessModel).flatMap { selectById(it!!) }

    override suspend fun selectById(id: Int) =
        businessModelRepository.selectById(id).map { it!!.toBusinessModel() }

    override suspend fun selectAll() =
        businessModelRepository.selectAll().map { list ->
            list.map { it.toBusinessModel() }.toBusinessModelList()
        }

    override suspend fun update(id: Int, businessModel: BusinessModelRequest) =
        businessModelRepository.update(id, businessModel).flatMap { selectById(id) }
}