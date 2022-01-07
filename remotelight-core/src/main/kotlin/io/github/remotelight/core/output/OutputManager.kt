package io.github.remotelight.core.output

import io.github.remotelight.core.output.config.OutputConfig
import io.github.remotelight.core.output.config.OutputConfigManager
import org.tinylog.kotlin.Logger
import java.util.*

class OutputManager(
    private val outputConfigManager: OutputConfigManager,
    private val outputRegistry: OutputRegistry
) {

    companion object {
        fun generateOutputId() = UUID.randomUUID().toString()
    }

    private val outputs = mutableListOf<Output>()

    init {
        outputConfigManager.attachOutputConfigSource(::getOutputConfigs)
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
        return createAndAddOutput(outputConfig)
    }

    fun createAndAddOutput(outputConfig: OutputConfig): Output {
        val output = createOutput(outputConfig)
        outputs.add(output)
        Logger.info("Created a new output ${outputConfig.id} (${outputConfig.outputIdentifier}).")
        onOutputAdded()
        return output
    }

    fun getOutputs() = outputs.toList()

    fun removeOutput(id: String): Boolean {
        val output = getOutputById(id) ?: throw IllegalArgumentException("No output for ID $id found.")
        return removeOutput(output)
    }

    fun removeOutput(output: Output): Boolean {
        output.config.destroy()
        val success = outputs.remove(output)
        onOutputRemoved()
        return success
    }

    private fun createOutput(config: OutputConfig): Output {
        return outputRegistry.createOutput(config.outputIdentifier, config)
            ?: throw IllegalArgumentException("Could not create output of type '${config.outputIdentifier}'.")
    }

    private fun createOutputConfig(outputIdentifier: OutputIdentifier): OutputConfig {
        val id = generateOutputId()
        return outputConfigManager.createOutputConfig(outputIdentifier, id)
    }

    private fun onOutputAdded() {
        storeOutputs()
    }

    private fun onOutputRemoved() {
        storeOutputs()
    }

}