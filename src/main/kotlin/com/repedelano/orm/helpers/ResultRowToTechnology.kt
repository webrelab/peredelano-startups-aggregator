package com.repedelano.orm.helpers

import com.repedelano.dtos.technology.TechnologyResponse
import com.repedelano.dtos.technology.TechnologyResponseList
import com.repedelano.orm.technology.Technologies
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toTechnology(): TechnologyResponse {
    return Technologies.run {
        TechnologyResponse(
            id = get(id).value,
            value = get(value)
        )
    }
}

fun List<TechnologyResponse>.toTechnologyResponseList() =
    TechnologyResponseList(this, size)