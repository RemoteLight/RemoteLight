package io.github.remotelight.api.models

import com.fasterxml.jackson.databind.JsonNode
import io.github.remotelight.core.output.OutputIdentifier
import io.github.remotelight.core.output.config.OutputConfig
import io.github.remotelight.core.output.config.OutputConfigWrapper

data class OutputConfigModel(
    val id: String?,
    val properties: Map<String, JsonNode>,
    val identifier: OutputIdentifier
)

fun OutputConfigModel.toWrapper(id: String) = OutputConfigWrapper(
    id, properties, identifier
)

@Suppress("UNCHECKED_CAST")
fun OutputConfig.toModel() = OutputConfigModel(
    id,
    getRawProperties() as Map<String, JsonNode>,
    outputIdentifier
)
