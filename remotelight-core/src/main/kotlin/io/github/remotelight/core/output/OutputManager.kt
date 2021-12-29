package io.github.remotelight.core.output

import io.github.remotelight.core.output.config.OutputConfig
import io.github.remotelight.core.output.config.OutputConfigManager
import org.tinylog.kotlin.Logger

class OutputManager(
    private val outputConfigManager: OutputConfigManager
) {

    private val outputs = mutableListOf<Output>()

    init {
        outputConfigManager.enableAutoSave(::getOutputConfigs)
        loadOutputs()
    }

    private fun getOutputConfigs(): List<OutputConfig> {
        return outputs.map { it.config }
    }

    @Synchronized
    private fun loadOutputs() {
        val outputConfigs = outputConfigManager.loadOutputConfigs()
        if (outputConfigs == null) {
            Logger.info("No outputs available.")
            return
        }

        outputConfigs.forEach { config ->
            val identifier = config.outputDescriptor.uniqueIdentifier
            val output = OutputRegistry.createOutput(identifier, config)

            if (output != null) {
                outputs.add(output)
                Logger.info("Loaded output ${config.name} ($identifier).")
            } else {
                Logger.warn("Failed to load output ${config.name}. Could not create output of type '$identifier'.")
            }
        }
    }

    @Synchronized
    fun storeOutputs() {
        outputConfigManager.storeOutputConfigs(getOutputConfigs())
    }

}