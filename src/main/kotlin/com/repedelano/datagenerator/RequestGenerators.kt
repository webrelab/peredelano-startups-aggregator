package com.repedelano.datagenerator

import com.repedelano.dtos.businessmodel.BusinessModelRequest
import com.repedelano.dtos.idea.IdeaRequest
import com.repedelano.dtos.projectroles.ProjectRoleRequest
import com.repedelano.dtos.scope.ScopeRequest
import com.repedelano.dtos.technology.TechnologyRequest
import com.repedelano.dtos.user.UserRequest
import com.repedelano.dtos.vacancy.VacancyRequest
import com.repedelano.dtos.vacancy.VacancyStatus
import com.repedelano.services.BusinessModelService
import com.repedelano.services.IdeaService
import com.repedelano.services.ProjectRoleService
import com.repedelano.services.ScopeService
import com.repedelano.services.TechnologyService
import com.repedelano.services.UserService
import com.repedelano.services.VacancyService
import io.github.serpro69.kfaker.Faker
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID
import kotlin.random.Random

object RequestGenerators {

    private const val SCOPES = "scopes"
    private const val BUSINESS_MODELS = "businessModels"
    private const val TECHNOLOGIES = "technologies"
    private const val OWNERS = "owners"
    private const val PROJECT_ROLES = "projectRoles"
    private const val IDEAS = "ideas"
    private const val VACANCIES = "vacancies"
    private val generatedIds = mutableMapOf<String, Int>()
    private val generatedIdeas = mutableListOf<UUID>()
    val faker = Faker()
    private val scopeService by inject<ScopeService>(ScopeService::class.java)
    private val bmService by inject<BusinessModelService>(BusinessModelService::class.java)
    private val techService by inject<TechnologyService>(TechnologyService::class.java)
    private val userService by inject<UserService>(UserService::class.java)
    private val ideaService by inject<IdeaService>(IdeaService::class.java)
    private val projectRoleService by inject<ProjectRoleService>(ProjectRoleService::class.java)
    private val vacancyService by inject<VacancyService>(VacancyService::class.java)

    fun generateScopes(count: Int) {
        runBlocking {
            repeat(count) {
                scopeService.addIfNotExists(getFakeScope())
            }
            generatedIds[SCOPES] = count
        }
    }

    fun generateBusinessModels(count: Int) {
        runBlocking {
            repeat(count) {
                bmService.insert(getFakeBusinessModel())
            }
            generatedIds[BUSINESS_MODELS] = count
        }
    }

    fun generateTechnologies(count: Int) {
        runBlocking {
            repeat(count) {
                techService.insertIfNotExists(getFakeTechnology())
            }
            generatedIds[TECHNOLOGIES] = count
        }
    }

    fun generateUsers(count: Int) {
        runBlocking {
            repeat(count) {
                userService.insert(getFakeUser())
            }
            generatedIds[OWNERS] = count
        }
    }

    fun generateIdeas(count: Int) {
        runBlocking {
            repeat(count) {
                ideaService.insert(getFakeIdea()).getOrNull()?.let {
                    generatedIdeas.add(it.id)
                }
            }
            generatedIds[IDEAS] = count
        }
    }

    fun generateProjectRoles(count: Int) {
        runBlocking {
            repeat(count) {
                projectRoleService.add(getFakeProjectRole())
            }
            generatedIds[PROJECT_ROLES] = count
        }
    }

    fun generateVacancies(count: Int) {
        runBlocking {
            repeat(count) {
                vacancyService.insert(
                    getFakeVacancy(
                        ideaId = getRandomIdeaForVacancy(count, it)
                    )
                )
            }
            repeat((count * 0.3).toInt()) {
                vacancyService.updateStatus(IdGen.nextUniqueId(VACANCIES, count), VacancyStatus.CLOSED)
            }
            repeat((count * 0.1).toInt()) {
                vacancyService.updateStatus(IdGen.nextUniqueId(VACANCIES, count), VacancyStatus.DECLINED)
            }
            generatedIds[VACANCIES] = count
        }
    }

    fun getFakeBusinessModel(
        value: String = faker.commerce.productName(),
        description: String = StringGen.nextString(3, 3)
    ) = BusinessModelRequest(value, description)

    fun getFakeProjectRole(
        name: String = faker.commerce.productName(),
        description: String = StringGen.nextString(3, 3)
    ) = ProjectRoleRequest(name, description)

    fun getFakeScope(
        value: String = faker.animal.unique.name(),
        description: String = StringGen.nextString(3, 3)
    ) = ScopeRequest(value, description)

    fun getFakeTechnology(
        value: String = faker.name.unique.firstName()
    ) = TechnologyRequest(value)

    fun getFakeUser(
        name: String = faker.name.firstName(),
        lastName: String = faker.name.lastName(),
        passportId: String = UUID.randomUUID().toString(),
        email: String = faker.internet.email("$lastName.$name"),
        tgUser: String = "@${lastName}_$name",
        picture: String = "/var/www/data/${name}_${lastName}.png"
    ) = UserRequest(
        passportId = passportId,
        name = name,
        lastName = lastName,
        email = email,
        tgUser = tgUser,
        picture = picture
    )

    fun getFakeIdea(
        owner: Int = IdGen.nextId(generatedIds.getValue(OWNERS)),
        title: String = faker.name.unique.name(),
        tgLink: String = "@${title.split(" ").joinToString("_")}",
        scopes: List<Int> = IdGen.nextUniqueIdList(
            "$owner $title $SCOPES",
            3,
            generatedIds.getOrDefault(SCOPES, 0)
        ),
        problem: String = StringGen.nextString(3, 1),
        description: String = StringGen.nextString(3, 5),
        businessModels: List<Int> = IdGen.nextUniqueIdList(
            "$owner $title $BUSINESS_MODELS",
            2,
            generatedIds.getValue(BUSINESS_MODELS)
        ),
        similarProjects: String = StringGen.nextString(1, 5),
        targetAudience: String = StringGen.nextString(2, 5),
        marketResearch: String = StringGen.nextString(3, 8),
        businessPlan: String = StringGen.nextString(8, 20),
        techStack: List<Int> = IdGen.nextUniqueIdList(
            "$owner $title $TECHNOLOGIES",
            10,
            generatedIds.getValue(TECHNOLOGIES)
        ),
        resources: String = StringGen.nextString(1, 5),
    ) = IdeaRequest(
        owner = owner,
        title = title,
        tgLink = tgLink,
        scopes = scopes,
        problem = problem,
        description = description,
        businessModels = businessModels,
        similarProjects = similarProjects,
        targetAudience = targetAudience,
        marketResearch = marketResearch,
        businessPlan = businessPlan,
        techStack = techStack,
        resources = resources
    )

    fun getFakeVacancy(
        ideaId: UUID = generatedIdeas[Random.nextInt(0, generatedIdeas.size)],
        projectRoleId: Int = IdGen.nextId(generatedIds.getValue(PROJECT_ROLES)),
        techStack: List<Int> = IdGen.nextUniqueIdList(6, generatedIds.getValue(TECHNOLOGIES)),
        description: String = StringGen.nextString(5, 5),
    ) = VacancyRequest(
        ideaId = ideaId,
        projectRoleId = projectRoleId,
        techStack = techStack,
        description = description
    )

    private fun getRandomIdeaForVacancy(total: Int, currentIndex: Int): UUID {
        val last = (.7 * generatedIdeas.size).toInt()
        val spreadIndex = (currentIndex * last / total).coerceAtLeast(1)
        val spread = generatedIdeas.subList(0, spreadIndex).size
        return generatedIdeas[Random.nextInt(0, spread)]
    }
}

