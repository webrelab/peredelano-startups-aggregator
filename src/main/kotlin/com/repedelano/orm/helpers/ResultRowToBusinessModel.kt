package com.repedelano.orm.helpers

import com.repedelano.dtos.businessmodel.BusinessModelResponse
import com.repedelano.dtos.businessmodel.BusinessModelResponseList
import com.repedelano.orm.businessmodel.BusinessModels
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toBusinessModel(): BusinessModelResponse {
    return BusinessModels.run {
        BusinessModelResponse(
            id = get(id).value,
            value = get(value),
            description = get(description)
        )
    }
}

fun List<BusinessModelResponse>.toBusinessModelList() =
    BusinessModelResponseList(this, size)