package io.github.remotelight.controller.output

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.remotelight.controller.model.OutputConfigModel
import io.github.remotelight.controller.model.toModel
import io.github.remotelight.controller.model.toWrapper
import io.github.remotelight.core.output.Output
import io.github.remotelight.core.output.OutputManager
import io.github.remotelight.core.output.config.JsonOutputConfigManager
import io.github.remotelight.core.output.config.OutputConfig
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class OutputController : KoinComponent {

    private val outputManager by inject<OutputManager>()
    private val jsonOutputConfigManager by inject<JsonOutputConfigManager>()
    private val objectMapper by inject<ObjectMapper>()

    fun getOutputConfigs(): List<OutputConfig> {
        return outputManager.getOutputs().map { it.config }
    }

    fun getOutputConfigsModel(): List<OutputConfigModel> {
        return outputManager.getOutputs().map { it.config.toModel() }
    }

    fun getOutputById(id: String) = outputManager.getOutputById(id)

    fun getOutputConfigModelById(id: String) = getOutputById(id)?.config?.toModel()

    fun createOutput(outputConfigModel: OutputConfigModel): Output {
        val id = OutputManager.generateOutputId()
        val outputConfig = jsonOutputConfigManager.createOutputConfig(outputConfigModel.toWrapper(id))
        return outputManager.createAndAddOutput(outputConfig)
    }

    fun updateOutput(id: String, outputConfigModel: OutputConfigModel): UpdateResult {
        if (outputConfigModel.id != null && outputConfigModel.id != id) {
            return UpdateResult.InvalidId
        }
        val existingOutputConfig = outputManager.getOutputById(id)?.config ?: return UpdateResult.NotFound
        if (outputConfigModel.identifier != existingOutputConfig.outputIdentifier) {
            return UpdateResult.InvalidOutputIdentifier
        }
        val properties = outputConfigModel.properties?.mapValues {
            val existing = existingOutputConfig.getProperties()[it.key]
            val type = existing?.javaClass ?: Any::class.java
            objectMapper.treeToValue(it.value, type)
        }
        if (!properties.isNullOrEmpty()) {
            existingOutputConfig.updateProperties(properties)
        }
        return UpdateResult.OutputUpdated(existingOutputConfig.toModel())
    }

    fun removeOutput(id: String) = outputManager.removeOutput(id)

    sealed class UpdateResult {
        object InvalidId: UpdateResult()
        object NotFound : UpdateResult()
        object InvalidOutputIdentifier : UpdateResult()
        data class OutputUpdated(val outputConfigModel: OutputConfigModel) : UpdateResult()
    }

}