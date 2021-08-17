package io.github.remotelight.core.di

import io.github.remotelight.core.config.loader.ConfigLoader
import io.github.remotelight.core.config.loader.JsonConfigLoader
import io.github.remotelight.core.constants.FilePaths
import io.github.remotelight.core.constants.FilePaths.mkdirsParents
import io.github.remotelight.core.constants.FilePaths.toFile
import kotlinx.coroutines.Job
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

object Modules {
    val coreModule = module {
        // the config loader that is used for the GlobalConfig
        single<ConfigLoader>(named("global")) { JsonConfigLoader(FilePaths.GLOBAL_CONFIG_PATH.toFile().mkdirsParents()) }
        // provide a shared coroutine context
        single<CoroutineContext> { Job() }
    }
}