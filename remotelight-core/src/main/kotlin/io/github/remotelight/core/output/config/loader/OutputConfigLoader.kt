package io.github.remotelight.core.output.config.loader

import io.github.remotelight.core.io.Loader
import io.github.remotelight.core.output.config.OutputConfigWrapper

interface OutputConfigLoader : Loader {

    fun loadOutputConfigs(): List<OutputConfigWrapper>?

    fun storeOutputConfigs(outputConfigs: List<OutputConfigWrapper>)

}