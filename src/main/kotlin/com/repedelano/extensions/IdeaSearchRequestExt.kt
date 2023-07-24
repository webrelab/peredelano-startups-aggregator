package com.repedelano.extensions

import com.repedelano.dtos.idea.IdeaSearchRequest

fun IdeaSearchRequest.isEmpty(): Boolean {
    return owner == null
        && queryString.isNullOrBlank()
        && scopes.isEmpty()
        && businessModels.isEmpty()
        && techStack.isEmpty()
}