package com.peredelano.ext

import com.repedelano.dtos.businessmodel.BusinessModelRequest
import com.repedelano.dtos.businessmodel.BusinessModelResponse
import com.repedelano.dtos.idea.IdeaRequest
import com.repedelano.dtos.idea.IdeaResponse
import com.repedelano.dtos.projectroles.ProjectRoleRequest
import com.repedelano.dtos.projectroles.ProjectRoleResponse
import com.repedelano.dtos.scope.ScopeRequest
import com.repedelano.dtos.scope.ScopeResponse
import com.repedelano.dtos.technology.TechnologyRequest
import com.repedelano.dtos.technology.TechnologyResponse
import com.repedelano.dtos.user.UserRequest
import com.repedelano.dtos.user.UserResponse
import com.repedelano.dtos.vacancy.VacancyRequest
import com.repedelano.dtos.vacancy.VacancyResponse
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
        { assertNullable(idea.owner, owner) },
        { assertNullable(idea.problem, problem) },
        { assertNullable(idea.title, title) },
        { assertNullable(idea.tgLink, tgLink) },
        { assertNullable(idea.description, description) },
        { assertNullable(idea.similarProjects, similarProjects) },
        { assertNullable(idea.targetAudience, targetAudience) },
        { assertNullable(idea.marketResearch, marketResearch) },
        { assertNullable(idea.businessPlan, businessPlan) },
        { assertNullable(idea.resources, resources) },
        { scopes.assertScopes(idea.scopes) },
        { businessModels.assertBusinessModels(idea.businessModels) },
        { techStack.assertTechnologies(idea.techStack) }
    )
}

fun VacancyResponse.assertAll(vacancy: VacancyRequest) {
    assertAll(
        { assertNullable(vacancy.ideaId, ideaId) },
        { techStack.assertTechnologies(vacancy.techStack) },
        { assertNullable(vacancy.description, description) },
        { assertNullable(vacancy.projectRoleId, projectRole.id) }
    )
}

fun BusinessModelResponse.assertAll(bm: BusinessModelRequest) {
    assertAll(
        { assertEquals(bm.value, value) },
        { assertEquals(bm.description, description) }
    )
}

fun ProjectRoleResponse.assertAll(pr: ProjectRoleRequest) {
    assertAll(
        { assertEquals(pr.name, name) },
        { assertEquals(pr.description, description) }
    )
}

fun ScopeResponse.assertAll(scope: ScopeRequest) {
    assertAll(
        { assertEquals(scope.value, value) },
        { assertEquals(scope.description, description) }
    )
}

fun TechnologyResponse.assertAll(technology: TechnologyRequest) {
    assertEquals(technology.value, value)
}

fun MutableList<ScopeResponse>.assertScopes(expectedScopesRequestList: List<Int>?) {
    if (expectedScopesRequestList == null) return
    val actual = map { it.id }
    assertEquals(expectedScopesRequestList.sorted(), actual.sorted(), "Scope assert failed")
}

fun MutableList<BusinessModelResponse>.assertBusinessModels(expectedBusinessModelsRequestList: List<Int>?) {
    if (expectedBusinessModelsRequestList == null) return
    val actual = map { it.id }
    assertEquals(expectedBusinessModelsRequestList.sorted(), actual.sorted(), "BusinessModel assert failed")
}

fun MutableList<TechnologyResponse>.assertTechnologies(expectedTechnologiesRequestList: List<Int>?) {
    if (expectedTechnologiesRequestList == null) return
    val actual = map { it.id }
    assertEquals(expectedTechnologiesRequestList.sorted(), actual.sorted(), "Technology assert failed")
}

private fun <T> assertNullable(expected: T?, actual: T?) {
    expected?.let { assertEquals(it, actual) }
}