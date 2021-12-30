package io.github.remotelight.core.output.config

import io.github.remotelight.core.config.ConfigChangeCallback
import io.github.remotelight.core.output.OutputIdentifier

data class OutputConfigWrapper(
    val id: String,
    val properties: Map<String, Any?>,
    val outputIdentifier: OutputIdentifier
)

fun OutputConfig.toOutputConfigWrapper() = OutputConfigWrapper(
    id = id,
    properties = getPropertyValues(),
    outputIdentifier = outputIdentifier
)

fun OutputConfigWrapper.toOutputConfig(
    configChangeCallback: ConfigChangeCallback,
    initializeProperties: Boolean = false
) = OutputConfig(
    configChangeCallback = configChangeCallback,
    outputIdentifier = outputIdentifier,
    id = id
).apply {
    if (initializeProperties) setPropertyValues(properties)
}
