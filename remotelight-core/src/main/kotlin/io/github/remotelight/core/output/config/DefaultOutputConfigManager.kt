package io.github.remotelight.core.output.config

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

class DefaultOutputConfigManager(
    private val outputConfigLoader: OutputConfigLoader
) : OutputConfigManager, KoinComponent {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val debounce by inject<Debounce<Unit>> { parametersOf(scope) }

    private var outputConfigSource: OutputConfigSource? = null

    override fun enableAutoSave(outputConfigSource: OutputConfigSource) {
        this.outputConfigSource = outputConfigSource
    }

    override fun loadOutputConfigs(): List<OutputConfig>? {
        val outputConfigWrappers = outputConfigLoader.loadOutputConfigs() ?: return null

        val outputConfigs = outputConfigWrappers.map { wrapper ->
            wrapper.toOutputConfig(this, true)
        }

        Logger.info("Loaded ${outputConfigs.size} output configs.")
        return outputConfigs
    }

    override fun storeOutputConfigs(outputConfigs: List<OutputConfig>) {
        debounce.debounce {
            Logger.trace("Storing ${outputConfigs.size} output configs to ${outputConfigLoader.getSource()}...")
            val outputConfigWrappers = outputConfigs.map { config ->
                config.toOutputConfigWrapper()
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
