package com.peredelano.ext

import com.repedelano.dtos.businessmodel.BusinessModelRequest
import com.repedelano.dtos.businessmodel.BusinessModelResponse
import com.repedelano.dtos.idea.IdeaRequest
import com.repedelano.dtos.idea.IdeaResponse
import com.repedelano.dtos.scope.ScopeResponse
import com.repedelano.dtos.technology.TechnologyResponse
import com.repedelano.dtos.user.UserRequest
import com.repedelano.dtos.user.UserResponse
import org.junit.jupiter.api.Assertions.*

fun UserResponse.assertAll(user: UserRequest) {
    assertAll(
        { assertEquals(user.passportId, passportId) },
        { assertEquals(user.email, email) },
        { assertEquals(user.tgUser, tgUser) },
        { assertEquals(user.name, name) },
        { assertEquals(user.lastName, lastName) },
        { assertEquals(user.picture, picture) }
    )
}

fun IdeaResponse.assertAll(idea: IdeaRequest) {
    assertAll(
        { assertEquals(idea.owner, owner) },
        { assertEquals(idea.problem, problem) },
        { idea.title?.let { assertEquals(it, title) } },
        { idea.tgLink?.let { assertEquals(it, tgLink) } },
        { idea.description?.let { assertEquals(it, description) } },
        { idea.similarProjects?.let { assertEquals(it, similarProjects) } },
        { idea.targetAudience?.let { assertEquals(it, targetAudience) } },
        { idea.marketResearch?.let { assertEquals(it, marketResearch) } },
        { idea.businessPlan?.let { assertEquals(it, businessPlan) } },
        { idea.resources?.let { assertEquals(it, resources) } },
        { idea.scopes?.let { scopes.assertScopes(it) } },
        { idea.businessModels?.let { businessModels.assertBusinessModels(it) } },
        { idea.techStack?.let { techStack.assertTechnologies(it) } }
    )
}

fun BusinessModelResponse.assertAll(bm: BusinessModelRequest) {
    assertAll(
        { assertEquals(bm.value, value) },
        { assertEquals(bm.description, description) }
    )
}

fun MutableList<ScopeResponse>.assertScopes(scopes: List<String>) {
    val expected = map { it.value }
    assertEquals(expected, scopes)
}

fun MutableList<BusinessModelResponse>.assertBusinessModels(businessModels: List<String>) {
    val expected = map { it.value }
    assertEquals(expected, businessModels)
}

fun MutableList<TechnologyResponse>.assertTechnologies(technologies: List<String>) {
    val expected = map { it.value }
    assertEquals(expected, technologies)
}