package io.github.remotelight.core.output

import io.github.remotelight.core.config.Config
import io.github.remotelight.core.config.Property
import io.github.remotelight.core.config.loader.ConfigLoader

class OutputConfig(private val loader: ConfigLoader): Config() {

    override fun createConfigLoader() = loader

    val name: String by Property("name", "")

    val pixels: Int by Property("pixels", 0)

}