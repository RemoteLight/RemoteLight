package io.github.remotelight.core.output

import io.github.remotelight.core.output.config.OutputConfig
import io.github.remotelight.core.output.config.OutputConfigManager
import org.tinylog.kotlin.Logger
import java.util.*

class OutputManager(
    private val outputConfigManager: OutputConfigManager,
    private val outputRegistry: OutputRegistry
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
            val identifier = config.outputIdentifier
            try {
                val output = createOutput(config)
                outputs.add(output)
                Logger.info("Loaded output ${config.id} (name: '${config.name}', type: $identifier).")
            } catch (e: IllegalArgumentException) {
                Logger.warn(e, "Failed to load output ${config.id} (name: '${config.name}').")
            }
        }
    }

    @Synchronized
    fun storeOutputs() {
        outputConfigManager.storeOutputConfigs(getOutputConfigs())
    }

    fun getOutputById(id: String) = outputs.find { it.config.id == id }

    fun createAndAddOutput(outputIdentifier: OutputIdentifier): Output {
        val outputConfig = createOutputConfig(outputIdentifier)
        val output = createOutput(outputConfig)
        outputs.add(output)
        Logger.info("Created a new output ${outputConfig.id} (${outputConfig.outputIdentifier}).")
        onOutputAdded()
        return output
    }

    fun getOutputs() = outputs.toList()

    fun removeOutput(id: String) {
        val output = getOutputById(id) ?: throw IllegalArgumentException("No output for ID $id found.")
        removeOutput(output)
    }

    fun removeOutput(output: Output) {
        output.config.destroy()
        outputs.remove(output)
        onOutputRemoved()
    }

    private fun createOutput(config: OutputConfig): Output {
        return outputRegistry.createOutput(config.outputIdentifier, config)
            ?: throw IllegalArgumentException("Could not create output of type '${config.outputIdentifier}'.")
    }

    private fun createOutputConfig(outputIdentifier: OutputIdentifier): OutputConfig {
        val id = UUID.randomUUID().toString()
        return OutputConfig(outputConfigManager, outputIdentifier, id)
    }

    private fun onOutputAdded() {
        storeOutputs()
    }

    private fun onOutputRemoved() {
        storeOutputs()
    }

}