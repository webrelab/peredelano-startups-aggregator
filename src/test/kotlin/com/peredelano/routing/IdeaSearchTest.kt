package com.peredelano.routing

import com.peredelano.BaseTest
import com.peredelano.ext.getConverted
import com.repedelano.datagenerator.RequestGenerators
import com.repedelano.dtos.idea.IdeaResponse
import com.repedelano.dtos.idea.IdeaResponseList
import com.repedelano.routes.IdeaRoutes.Companion.clientIdeaSearch
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class IdeaSearchTest : BaseTest() {

    companion object {

        @JvmStatic
        @BeforeAll
        fun ideaSetUp() = with(RequestGenerators) {
            generateScopes(10)
            generateBusinessModels(10)
            generateTechnologies(10)
            generateUsers(10)
            generateIdeas(4000)
        }

        @JvmStatic
        fun scopeData(): List<Arguments> = listOf(
            Arguments.of(listOf(2)),
            Arguments.of(listOf(8, 4)),
            Arguments.of(listOf(1, 7, 10)),
        )

        @JvmStatic
        fun bmData(): List<Arguments> = listOf(
            Arguments.of(listOf(5)),
            Arguments.of(listOf(10, 1)),
        )

        @JvmStatic
        fun techData(): List<Arguments> = listOf(
            Arguments.of(listOf(5)),
            Arguments.of(listOf(10, 1)),
            Arguments.of(listOf(2, 5, 3)),
        )
    }

    @ParameterizedTest
    @MethodSource("scopeData")
    fun searchByScope(scopeData: List<Int>) = with(testEngine) {
        val result: IdeaResponseList = getConverted(
            clientIdeaSearch(
                scopes = scopeData
            )
        )
        assertTrue(result.ideas.isNotEmpty(), "Ideas not found")
        assertTrue(
            result.ideas
                .map { it.scopes.map { s -> s.id } }
                .all { it.containsAll(scopeData) },
            "Not all ideas contains searched data"
        )
    }

    @ParameterizedTest
    @MethodSource("bmData")
    fun searchByBusinessModel(bmData: List<Int>) = with(testEngine) {
        val result: IdeaResponseList = getConverted(
            clientIdeaSearch(
                businessModels = bmData
            )
        )
        assertTrue(result.ideas.isNotEmpty(), "Ideas not found")
        assertTrue(
            result.ideas
                .map { it.businessModels.map { s -> s.id } }
                .all { it.containsAll(bmData) },
            "Not all ideas contains searched data"
        )
    }

    @ParameterizedTest
    @MethodSource("techData")
    fun searchByTechnologies(techData: List<Int>) = with(testEngine) {
        val result: IdeaResponseList = getConverted(
            clientIdeaSearch(
                techStack = techData
            )
        )
        assertTrue(result.ideas.isNotEmpty(), "Ideas not found")
        assertTrue(
            result.ideas
                .map { it.techStack.map { s -> s.id } }
                .all { it.containsAll(techData) },
            "Not all ideas contains searched data"
        )
    }

    @Test
    fun searchByOwner() = with(testEngine) {
        val result: IdeaResponseList = getConverted(
            clientIdeaSearch(
                owner = 8
            )
        )
        assertTrue(result.ideas.isNotEmpty(), "Ideas not found")
        assertTrue(
            result.ideas
                .all { it.owner == 8 },
            "Not all ideas contains searched data"
        )
    }

    @ParameterizedTest
    @MethodSource("scopeData")
    fun searchByOwnerAndScopes(scopeData: List<Int>) = with(testEngine) {
        val result: IdeaResponseList = getConverted(
            clientIdeaSearch(
                owner = 3,
                scopes = scopeData
            )
        )
        assertTrue(result.ideas.isNotEmpty(), "Ideas not found")
        assertTrue(
            result.ideas
                .all {
                    it.owner == 3
                        && it.scopes
                        .map { s -> s.id }
                        .containsAll(scopeData)
                },
            "Not all ideas contains searched data"
        )
    }

    @ParameterizedTest
    @MethodSource("scopeData")
    fun searchByScopeAndTech(data: List<Int>) = with(testEngine) {
        val result: IdeaResponseList = getConverted(
            clientIdeaSearch(
                scopes = data,
                techStack = data
            )
        )
        assertTrue(result.ideas.isNotEmpty(), "Ideas not found")
        assertTrue(
            result.ideas
                .all {
                    it.scopes
                        .map { s -> s.id }
                        .containsAll(data)
                        && it.techStack
                        .map { t -> t.id }
                        .containsAll(data)
                },
            "Not all ideas contains searched data"
        )
    }

    @Test
    fun searchByWord() = with(testEngine) {
        val word = "Prothero"
        val result: IdeaResponseList = getConverted(
            clientIdeaSearch(
                queryString = word
            )
        )
        assertTrue(result.ideas.isNotEmpty(), "Ideas not found")
        assertTrue(
            result.ideas
                .all { ideaContainsWord(it, word) },
            "Not all ideas contains searched data"
        )
    }

    @Test
    fun searchByOwnerAndWord() = with(testEngine) {
        val word = "Prothero"
        val result: IdeaResponseList = getConverted(
            clientIdeaSearch(
                owner = 3,
                queryString = word
            )
        )
        assertTrue(result.ideas.isNotEmpty(), "Ideas not found")
        assertTrue(
            result.ideas
                .all { it.owner == 3 && ideaContainsWord(it, word) }
        )
    }

    @ParameterizedTest
    @MethodSource("scopeData")
    fun searchByWordAndScopes(scopeData: List<Int>) = with(testEngine) {
        val word = "truth"
        val result: IdeaResponseList = getConverted(
            clientIdeaSearch(
                queryString = word,
                scopes = scopeData
            )
        )
        assertTrue(result.ideas.isNotEmpty(), "Ideas not found")
        assertTrue(
            result.ideas
                .all {
                    it.scopes
                        .map { s -> s.id }
                        .containsAll(scopeData)
                        && ideaContainsWord(it, word)
                },
        )
    }

    private fun ideaContainsWord(idea: IdeaResponse, word: String): Boolean {
        return listOfNotNull(
            idea.businessPlan,
            idea.problem,
            idea.description,
            idea.marketResearch,
            idea.resources,
            idea.similarProjects,
            idea.targetAudience,
            idea.tgLink,
            idea.title
        ).any { s -> s.contains(word, true) }
    }
}