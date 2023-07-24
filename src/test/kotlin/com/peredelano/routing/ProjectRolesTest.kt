package com.peredelano.routing

import com.peredelano.BaseTest
import com.peredelano.ext.assertAll
import com.peredelano.ext.getConverted
import com.peredelano.ext.postConverted
import com.peredelano.ext.postCreated
import com.peredelano.ext.putOk
import com.repedelano.datagenerator.RequestGenerators.getFakeProjectRole
import com.repedelano.dtos.projectroles.ProjectRoleRequest
import com.repedelano.dtos.projectroles.ProjectRoleResponse
import com.repedelano.dtos.projectroles.ProjectRoleResponseList
import com.repedelano.routes.ProjectRolesRoutes.Companion.ADD_PR
import com.repedelano.routes.ProjectRolesRoutes.Companion.PRS
import com.repedelano.routes.ProjectRolesRoutes.Companion.clientPrWithId
import com.repedelano.routes.ProjectRolesRoutes.Companion.clientPrsSearch
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class ProjectRolesTest : BaseTest() {

    companion object {

        val data1 = ProjectRoleRequest(
            "Backend engineer",
            "Is responsible for designing, building, SPECIAL CONTENT FOR SEARCH BACK and maintaining the server side of web applications"
        )
        val data2 = ProjectRoleRequest(
            "Frontend engineer",
            "Plan, design, build, and implement the user interface systems of websites, SPECIAL CONTENT FOR SEARCH FRONT software programs, and web-based application"
        )

        @JvmStatic
        @BeforeAll
        fun projectRoleSetUp(): Unit = with(testEngine) {
            repeat(10) { postCreated(ADD_PR, getFakeProjectRole()) }
            postCreated(ADD_PR, data1)
            postCreated(ADD_PR, data2)
        }
    }

    @Test
    fun addProjectRole() = with(testEngine) {
        val data = getFakeProjectRole()
        val response: ProjectRoleResponse = postConverted(ADD_PR, data)
        response.assertAll(data)
    }

    @Test
    fun getById() = with(testEngine) {
        val data = getFakeProjectRole()
        val responseId = postConverted<ProjectRoleRequest, ProjectRoleResponse>(ADD_PR, data).id
        val getById: ProjectRoleResponse = getConverted(clientPrWithId(responseId))
        getById.assertAll(data)
    }

    @Test
    fun searchInDescription() = with(testEngine) {
        val search: ProjectRoleResponseList = getConverted(clientPrsSearch("search front"))
        assertEquals(1, search.count)
        search.projectRoles[0].assertAll(data2)
    }

    @Test
    fun searchInName() = with(testEngine) {
        val search: ProjectRoleResponseList = getConverted(clientPrsSearch("backend eng"))
        assertEquals(1, search.count)
        search.projectRoles[0].assertAll(data1)
    }

    @Test
    fun searchMultipleRecords() = with(testEngine) {
        val search: ProjectRoleResponseList = getConverted(clientPrsSearch("special content"))
        assertEquals(2, search.count)
        search.projectRoles.find { it.name == data1.name }?.assertAll(data1)
            ?: throw java.lang.AssertionError("Item 'Backend engineer' not found")
        search.projectRoles.find { it.name == data2.name }?.assertAll(data2)
            ?: throw java.lang.AssertionError("Item 'Frontend engineer' not found")
    }

    @Test
    fun searchNotFound() = with(testEngine) {
        val search: ProjectRoleResponseList = getConverted(clientPrsSearch("wrong search query"))
        assertEquals(0, search.count)
    }

    @Test
    fun getAll() = with(testEngine) {
        val response: ProjectRoleResponseList = getConverted(PRS)
        assertTrue(response.count >= 10)
    }

    @Test
    fun update() = with(testEngine) {
        val data = getFakeProjectRole()
        putOk(clientPrWithId(4), data)
        val response: ProjectRoleResponse = getConverted(clientPrWithId(4))
        response.assertAll(data)
    }
}