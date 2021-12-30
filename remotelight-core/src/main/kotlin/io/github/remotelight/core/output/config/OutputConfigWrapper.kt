package io.github.remotelight.core.output.config

import io.github.remotelight.core.output.OutputIdentifier

data class OutputConfigWrapper(
    val properties: Map<String, Any?>,
    val outputIdentifier: OutputIdentifier
)
