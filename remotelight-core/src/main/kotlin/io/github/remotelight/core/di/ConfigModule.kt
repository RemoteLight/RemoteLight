package io.github.remotelight.core.di

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import io.github.remotelight.core.config.loader.JsonConfigLoader
import io.github.remotelight.core.config.loader.JsonPropertySource
import io.github.remotelight.core.config.provider.JsonPropertyProvider
import io.github.remotelight.core.config.provider.PropertyProvider
import io.github.remotelight.core.constants.Defaults
import io.github.remotelight.core.constants.FilePaths
import io.github.remotelight.core.constants.FilePaths.mkdirsParents
import io.github.remotelight.core.constants.FilePaths.toFile
import io.github.remotelight.core.utils.CoolDownDebounce
import io.github.remotelight.core.utils.Debounce
import kotlinx.coroutines.CoroutineScope
import org.koin.core.qualifier.named
import org.koin.dsl.module

val configModule = module {
    single { createObjectMapper() }
    single<PropertyProvider<*>>(named("global")) { JsonPropertyProvider(get(), get(named("global"))) }
    single<JsonPropertySource>(named("global")) {
        JsonConfigLoader(
            FilePaths.GLOBAL_CONFIG_PATH.toFile().mkdirsParents(),
            get()
        )
    }
    factory<Debounce<Unit>> { (scope: CoroutineScope) -> CoolDownDebounce(Defaults.CONFIG_STORE_DEBOUNCE_DELAY, scope) }
}

private fun createObjectMapper() = jacksonMapperBuilder()
    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
    .build()
    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
    .enable(SerializationFeature.INDENT_OUTPUT)
