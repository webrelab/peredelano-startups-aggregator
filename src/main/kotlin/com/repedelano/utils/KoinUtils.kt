package com.repedelano.utils

import com.repedelano.repositories.BusinessModelRepository
import com.repedelano.repositories.BusinessModelRepositoryImpl
import com.repedelano.repositories.IdeaBusinessModelsRepository
import com.repedelano.repositories.IdeaBusinessModelsRepositoryImpl
import com.repedelano.repositories.IdeaRepository
import com.repedelano.repositories.IdeaRepositoryImpl
import com.repedelano.repositories.IdeaScopesRepository
import com.repedelano.repositories.IdeaScopesRepositoryImpl
import com.repedelano.repositories.IdeaTechnologiesRepository
import com.repedelano.repositories.IdeaTechnologiesRepositoryImpl
import com.repedelano.repositories.ProjectRoleRepository
import com.repedelano.repositories.ProjectRoleRepositoryImpl
import com.repedelano.repositories.ScopeRepository
import com.repedelano.repositories.ScopeRepositoryImpl
import com.repedelano.repositories.TechnologyRepository
import com.repedelano.repositories.TechnologyRepositoryImpl
import com.repedelano.repositories.UserRepository
import com.repedelano.repositories.UserRepositoryImpl
import com.repedelano.repositories.VacancyRepository
import com.repedelano.repositories.VacancyRepositoryImpl
import com.repedelano.repositories.VacancyTechnologiesRepository
import com.repedelano.repositories.VacancyTechnologiesRepositoryImpl
import com.repedelano.services.BusinessModelService
import com.repedelano.services.BusinessModelServiceImpl
import com.repedelano.services.IdeaService
import com.repedelano.services.IdeaServiceImpl
import com.repedelano.services.ProjectRoleService
import com.repedelano.services.ProjectRoleServiceImpl
import com.repedelano.services.ScopeService
import com.repedelano.services.ScopeServiceImpl
import com.repedelano.services.TechnologyService
import com.repedelano.services.TechnologyServiceImpl
import com.repedelano.services.UserService
import com.repedelano.services.UserServiceImpl
import com.repedelano.services.VacancyService
import com.repedelano.services.VacancyServiceImpl
import com.repedelano.usecases.addBusinessModelUseCase
import com.repedelano.usecases.addIdeaUseCase
import com.repedelano.usecases.addProjectRoleUseCase
import com.repedelano.usecases.addScopeUseCase
import com.repedelano.usecases.addUserUseCase
import com.repedelano.usecases.getBusinessModelByIdUseCase
import com.repedelano.usecases.getBusinessModelsUseCase
import com.repedelano.usecases.getIdeaUseCase
import com.repedelano.usecases.getIdeasUseCase
import com.repedelano.usecases.getProjectRoleByIdUseCase
import com.repedelano.usecases.getProjectRolesUseCase
import com.repedelano.usecases.getScopeByIdUseCase
import com.repedelano.usecases.getScopesUseCase
import com.repedelano.usecases.getUserByIdUseCase
import com.repedelano.usecases.getUsersUseCase
import com.repedelano.usecases.searchProjectRoleUseCase
import com.repedelano.usecases.searchScopeUseCase
import com.repedelano.usecases.searchUserUseCase
import com.repedelano.usecases.updateBusinessModelUseCase
import com.repedelano.usecases.updateIdeaStageUseCase
import com.repedelano.usecases.updateProjectRoleUseCase
import com.repedelano.usecases.updateIdeaUseCase
import com.repedelano.usecases.updateScopeUseCase
import com.repedelano.usecases.updateUserUseCase
import com.repedelano.utils.db.DbFactory
import com.repedelano.utils.db.DbTransaction
import io.ktor.server.application.ApplicationEnvironment
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val IO_DISPATCHER = "IODispatcher"
private const val MAIN_DISPATCHER = "MainDispatcher"
private const val DEFAULT_DISPATCHER = "DefaultDispatcher"

fun KoinApplication.configure(environment: Module) {
    allowOverride(true)
    modules(koinModules(environment))
}

fun environmentSetter(environment: ApplicationEnvironment) = module {
    single { environment }
}

private fun koinModules(environment: Module) =
    listOf(environment, dispatchers, mainModule, repositories, services, useCases)

private fun environment(env: String) = module {
    single { env }
}

private val dispatchers = module {
    single(named(IO_DISPATCHER)) { Dispatchers.IO }
    single(named(MAIN_DISPATCHER)) { Dispatchers.Main }
    single(named(DEFAULT_DISPATCHER)) { Dispatchers.Default }
}

private val useCases = module {
    // business model use cases
    factory { addBusinessModelUseCase(get(named(IO_DISPATCHER)), get()) }
    factory { getBusinessModelByIdUseCase(get(named(IO_DISPATCHER)), get()) }
    factory { getBusinessModelsUseCase(get(named(IO_DISPATCHER)), get()) }
    factory { updateBusinessModelUseCase(get(named(IO_DISPATCHER)), get()) }

    // idea use-cases
    factory { addIdeaUseCase(get(named(IO_DISPATCHER)), get()) }
    factory { getIdeaUseCase(get(named(IO_DISPATCHER)), get()) }
    factory { getIdeasUseCase(get(named(IO_DISPATCHER)), get())}
    factory { updateIdeaUseCase(get(named(IO_DISPATCHER)), get())}
    factory { updateIdeaStageUseCase(get(named(IO_DISPATCHER)), get())}

    // project role use-cases
    factory { addProjectRoleUseCase(get(named(IO_DISPATCHER)), get()) }
    factory { getProjectRoleByIdUseCase(get(named(IO_DISPATCHER)), get()) }
    factory { searchProjectRoleUseCase(get(named(IO_DISPATCHER)), get()) }
    factory { getProjectRolesUseCase(get(named(IO_DISPATCHER)), get()) }
    factory { updateProjectRoleUseCase(get(named(IO_DISPATCHER)), get()) }

    // scope use cases
    factory { addScopeUseCase(get(named(IO_DISPATCHER)), get()) }
    factory { getScopeByIdUseCase(get(named(IO_DISPATCHER)), get()) }
    factory { searchScopeUseCase(get(named(IO_DISPATCHER)), get()) }
    factory { getScopesUseCase(get(named(IO_DISPATCHER)), get()) }
    factory { updateScopeUseCase(get(named(IO_DISPATCHER)), get()) }

    // user use-cases
    factory { addUserUseCase(get(named(IO_DISPATCHER)), get()) }
    factory { getUserByIdUseCase(get(named(IO_DISPATCHER)), get()) }
    factory { searchUserUseCase(get(named(IO_DISPATCHER)), get()) }
    factory { getUsersUseCase(get(named(IO_DISPATCHER)), get()) }
    factory { updateUserUseCase(get(named(IO_DISPATCHER)), get()) }
}

private val repositories = module {
    single<BusinessModelRepository> { BusinessModelRepositoryImpl(get()) }
    single<IdeaBusinessModelsRepository> { IdeaBusinessModelsRepositoryImpl(get()) }
    single<IdeaRepository> { IdeaRepositoryImpl(get()) }
    single<IdeaScopesRepository> { IdeaScopesRepositoryImpl(get()) }
    single<IdeaTechnologiesRepository> { IdeaTechnologiesRepositoryImpl(get()) }
    single<ProjectRoleRepository> { ProjectRoleRepositoryImpl(get()) }
    single<ScopeRepository> { ScopeRepositoryImpl(get()) }
    single<TechnologyRepository> { TechnologyRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<VacancyRepository> { VacancyRepositoryImpl(get()) }
    single<VacancyTechnologiesRepository> { VacancyTechnologiesRepositoryImpl(get()) }
}

private val services = module {
    single<BusinessModelService> { BusinessModelServiceImpl(get()) }
    single<IdeaService> {
        IdeaServiceImpl(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
    single<ProjectRoleService> { ProjectRoleServiceImpl(get()) }
    single<ScopeService> { ScopeServiceImpl(get()) }
    single<TechnologyService> { TechnologyServiceImpl(get()) }
    single<UserService> { UserServiceImpl(get()) }
    single<VacancyService> {
        VacancyServiceImpl(
            get(),
            get(),
            get(),
        )
    }
}

private val mainModule = module {
    single { params ->
        DbFactory(params.get())
    }
    single { DbTransaction(get(named(IO_DISPATCHER))) }
    single {
        Json {
            encodeDefaults = true
            isLenient = true
            prettyPrint = false
            coerceInputValues = true
            explicitNulls = false
        }
    }
}