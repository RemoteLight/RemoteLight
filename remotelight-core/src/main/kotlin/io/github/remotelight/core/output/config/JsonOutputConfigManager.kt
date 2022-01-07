package io.github.remotelight.core.output.config

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.remotelight.core.config.provider.JsonPropertyProvider
import io.github.remotelight.core.output.OutputIdentifier
import io.github.remotelight.core.output.config.loader.OutputWrapperLoader
import io.github.remotelight.core.utils.Debounce
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.tinylog.kotlin.Logger

class JsonOutputConfigManager(
    private val objectMapper: ObjectMapper,
    private val outputWrapperLoader: OutputWrapperLoader<JsonNode>,
) : OutputConfigManager, OutputConfigLoadStoreSource, KoinComponent {

    private var outputConfigSource: OutputConfigSource? = null

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val debounce by inject<Debounce<Unit>> { parametersOf(scope) }

    override fun attachOutputConfigSource(outputConfigSource: OutputConfigSource) {
        this.outputConfigSource = outputConfigSource
    }

    override fun loadOutputConfigs(): List<OutputConfig>? {
        val wrappers = loadOutputConfigWrapper() ?: return null
        return initializeOutputConfigs(wrappers).also {
            Logger.info("Loaded ${it.size} output configs.")
        }
    }

    private fun initializeOutputConfigs(wrappers: List<OutputConfigWrapper<JsonNode>>): List<OutputConfig> {
        return wrappers.map { wrapper ->
            createOutputConfig(
                properties = wrapper.properties,
                outputIdentifier = wrapper.outputIdentifier,
                id = wrapper.id
            )
        }
    }

    override fun createOutputConfig(outputIdentifier: OutputIdentifier, id: String): OutputConfig {
        return createOutputConfig(outputIdentifier, id, emptyMap())
    }

    fun createOutputConfig(outputConfigWrapper: OutputConfigWrapper<JsonNode>): OutputConfig {
        return with(outputConfigWrapper) {
            createOutputConfig(outputIdentifier, id, properties)
        }
    }

    fun createOutputConfig(
        outputIdentifier: OutputIdentifier,
        id: String,
        properties: Map<String, JsonNode>
    ): OutputConfig {
        val propertySource = JsonOutputPropertySource(id, this)
        return OutputConfig(
            propertyProvider = JsonPropertyProvider(objectMapper, propertySource, properties),
            outputIdentifier = outputIdentifier,
            id = id
        )
    }

    private fun loadOutputConfigWrapper(): List<OutputConfigWrapper<JsonNode>>? {
        return outputWrapperLoader.loadOutputWrappers()
    }

    override fun storeOutputConfigs(outputConfigs: List<OutputConfig>) {
        debounce.debounce {
            Logger.trace("Storing ${outputConfigs.size} output configs to ${outputWrapperLoader.getSource()}...")
            val outputConfigWrappers = outputConfigs.map { config ->
                OutputConfigWrapper(
                    id = config.id,
                    properties = config.getRawProperties() as Map<String, JsonNode>,
                    outputIdentifier = config.outputIdentifier
                )
            }
            outputWrapperLoader.storeOutputWrappers(outputConfigWrappers)
            Logger.trace("Successfully stored output configs.")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getPropertyValuesForOutput(outputId: String): Map<String, JsonNode> {
        return outputConfigSource?.invoke()
            ?.find { it.id == outputId }
            ?.getProperties().orEmpty() as Map<String, JsonNode>
    }

    override fun storePropertyValuesForOutput(outputId: String, properties: Map<String, JsonNode>) {
        outputConfigSource?.invoke()?.let { storeOutputConfigs(it) }
    }

    fun cancelScope() {
        scope.cancel()
    }

}