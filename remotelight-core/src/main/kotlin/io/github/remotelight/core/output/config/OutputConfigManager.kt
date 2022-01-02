package io.github.remotelight.core.output.config

import io.github.remotelight.core.output.OutputIdentifier

interface OutputConfigManager {

    fun attachOutputConfigSource(outputConfigSource: OutputConfigSource)

    fun loadOutputConfigs(): List<OutputConfig>?

    fun storeOutputConfigs(outputConfigs: List<OutputConfig>)

    fun createOutputConfig(outputIdentifier: OutputIdentifier, id: String): OutputConfig

}

typealias OutputConfigSource = () -> List<OutputConfig>