package io.github.remotelight.core.output.config

import io.github.remotelight.core.output.OutputIdentifier

data class OutputConfigWrapper<T>(
    val id: String,
    val properties: Map<String, T>,
    val outputIdentifier: OutputIdentifier
)
