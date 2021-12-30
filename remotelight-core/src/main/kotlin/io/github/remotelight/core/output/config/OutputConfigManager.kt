package io.github.remotelight.core.output.config

import io.github.remotelight.core.config.ConfigChangeCallback

interface OutputConfigManager : ConfigChangeCallback {

    fun enableAutoSave(outputConfigSource: OutputConfigSource)

    fun loadOutputConfigs(): List<OutputConfig>?

    fun storeOutputConfigs(outputConfigs: List<OutputConfig>)

}

typealias OutputConfigSource = () -> List<OutputConfig>