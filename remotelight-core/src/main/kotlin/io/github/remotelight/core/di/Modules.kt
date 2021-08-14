package io.github.remotelight.core.di

import io.github.remotelight.core.config.constants.FilePaths
import io.github.remotelight.core.config.constants.FilePaths.mkdirsParents
import io.github.remotelight.core.config.constants.FilePaths.toFile
import io.github.remotelight.core.config.loader.ConfigLoader
import io.github.remotelight.core.config.loader.JsonConfigLoader
import kotlinx.coroutines.Job
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

object Modules {
    val coreModule = module {
        single<ConfigLoader>(named("global")) { JsonConfigLoader(FilePaths.GLOBAL_CONFIG_PATH.toFile().mkdirsParents()) }
        single<CoroutineContext> { Job() }
    }
}