package io.github.remotelight.core.di

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import io.github.remotelight.core.config.ConfigChangeCallback
import io.github.remotelight.core.config.ConfigManager
import io.github.remotelight.core.config.loader.ConfigLoader
import io.github.remotelight.core.config.loader.JsonConfigLoader
import io.github.remotelight.core.constants.Defaults
import io.github.remotelight.core.constants.FilePaths
import io.github.remotelight.core.constants.FilePaths.mkdirsParents
import io.github.remotelight.core.constants.FilePaths.toFile
import io.github.remotelight.core.utils.CoolDownDebounce
import io.github.remotelight.core.utils.Debounce
import kotlinx.coroutines.CoroutineScope
import org.koin.core.qualifier.named
import org.koin.dsl.binds
import org.koin.dsl.module

val configModule = module {
    single { createObjectMapper() }
    single<ConfigLoader>(named("global")) {
        JsonConfigLoader(
            FilePaths.GLOBAL_CONFIG_PATH.toFile().mkdirsParents(),
            get()
        )
    }
    single(named("global")) { ConfigManager(get(named("global"))) } binds arrayOf(
        ConfigChangeCallback::class,
        ConfigManager::class
    )
    factory<Debounce<Unit>> { (scope: CoroutineScope) -> CoolDownDebounce(Defaults.CONFIG_STORE_DEBOUNCE_DELAY, scope) }
}

private fun createObjectMapper() = jacksonMapperBuilder()
    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
    .build()
    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
    .enable(SerializationFeature.INDENT_OUTPUT)
