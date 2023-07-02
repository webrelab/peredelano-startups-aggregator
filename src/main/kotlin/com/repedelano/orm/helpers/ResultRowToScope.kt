package com.repedelano.orm.helpers

import com.repedelano.dtos.scope.ScopeResponse
import com.repedelano.dtos.scope.ScopeResponseList
import com.repedelano.orm.scope.Scopes
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toScope(): ScopeResponse {
    return Scopes.run {
        ScopeResponse(
            id = get(id).value,
            value = get(value),
            description = get(description)
        )
    }
}

fun List<ScopeResponse>.toScopeResponseList() = ScopeResponseList(this, size)