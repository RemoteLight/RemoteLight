package io.github.remotelight.core.di

import com.fasterxml.jackson.databind.JsonNode
import io.github.remotelight.core.constants.FilePaths
import io.github.remotelight.core.constants.FilePaths.mkdirsParents
import io.github.remotelight.core.constants.FilePaths.toFile
import io.github.remotelight.core.output.DefaultOutputRegistry
import io.github.remotelight.core.output.OutputManager
import io.github.remotelight.core.output.OutputRegistry
import io.github.remotelight.core.output.config.JsonOutputConfigManager
import io.github.remotelight.core.output.config.OutputConfigManager
import io.github.remotelight.core.output.config.loader.JsonOutputWrapperLoader
import io.github.remotelight.core.output.config.loader.OutputWrapperLoader
import io.github.remotelight.core.output.scene.SceneManager
import io.github.remotelight.core.output.scene.loader.JsonSceneLoader
import io.github.remotelight.core.output.scene.loader.SceneLoader
import org.koin.dsl.binds
import org.koin.dsl.module

val outputModule = module {
    factory<OutputWrapperLoader<JsonNode>> {
        JsonOutputWrapperLoader(
            FilePaths.OUTPUTS_CONFIG_PATH.toFile().mkdirsParents(), get()
        )
    }
    single { JsonOutputConfigManager(get(), get()) } binds arrayOf(
        OutputConfigManager::class,
        JsonOutputConfigManager::class
    )
    single { OutputManager(get(), get()) }
    single<OutputRegistry> { DefaultOutputRegistry }
    // Scenes
    factory<SceneLoader> { JsonSceneLoader(FilePaths.SCENES_FILE_PATH.toFile().mkdirsParents(), get()) }
    single { SceneManager(get()).apply { loadScenes() } }
}
