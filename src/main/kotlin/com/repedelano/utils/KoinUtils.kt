package com.repedelano.utils

import kotlinx.coroutines.Dispatchers
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val IO_DISPATCHER = "IODispatcher"
private const val MAIN_DISPATCHER = "MainDispatcher"
private const val DEFAULT_DISPATCHER = "DefaultDispatcher"

fun KoinApplication.configure(koinModules: List<Module>) {
    allowOverride(true)
    modules(koinModules)
}

fun koinModules() = listOf(dispatchers, mainModule)

private val dispatchers = module {
    single(named(IO_DISPATCHER)) { Dispatchers.IO }
    single(named(MAIN_DISPATCHER)) { Dispatchers.Main }
    single(named(DEFAULT_DISPATCHER)) { Dispatchers.Default }
}

private val mainModule = module {

}