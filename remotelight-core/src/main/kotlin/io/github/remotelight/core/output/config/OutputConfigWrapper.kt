package io.github.remotelight.core.output.config

import io.github.remotelight.core.output.OutputDescriptor

data class OutputConfigWrapper(
    val properties: Map<String, Any?>,
    val outputDescriptor: OutputDescriptor
)
