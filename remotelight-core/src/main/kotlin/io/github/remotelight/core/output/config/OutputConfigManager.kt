package io.github.remotelight.core.output.config

import io.github.remotelight.core.config.ConfigChangeCallback
import io.github.remotelight.core.output.config.loader.OutputConfigLoader
import io.github.remotelight.core.utils.Debounce
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.tinylog.kotlin.Logger

class OutputConfigManager(
    private val outputConfigLoader: OutputConfigLoader
) : ConfigChangeCallback, KoinComponent {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val debounce by inject<Debounce<Unit>> { parametersOf(scope) }

    private var outputConfigSource: OutputConfigSource? = null

    fun enableAutoSave(outputConfigSource: OutputConfigSource) {
        this.outputConfigSource = outputConfigSource
    }

    fun loadOutputConfigs(): List<OutputConfig>? {
        val outputConfigWrappers = outputConfigLoader.loadOutputConfigs() ?: return null

        val outputConfigs = outputConfigWrappers.map { wrapper ->
            OutputConfig(this, wrapper.outputDescriptor).apply {
                setPropertyValues(wrapper.properties)
            }
        }

        Logger.info("Loaded ${outputConfigs.size} output configs.")
        return outputConfigs
    }

    fun storeOutputConfigs(outputConfigs: List<OutputConfig>) {
        debounce.debounce {
            Logger.trace("Storing ${outputConfigs.size} output configs to ${outputConfigLoader.getSource()}...")
            val outputConfigWrappers = outputConfigs.map { config ->
                OutputConfigWrapper(config.getPropertyValues(), config.outputDescriptor)
            }
            outputConfigLoader.storeOutputConfigs(outputConfigWrappers)
            Logger.trace("Successfully stored output configs.")
        }
    }

    override fun onConfigChange(properties: Map<String, Any?>) {
        outputConfigSource?.let { source ->
            storeOutputConfigs(source())
        }
    }

    fun cancelScope() {
        scope.cancel()
    }

}

typealias OutputConfigSource = () -> List<OutputConfig>
