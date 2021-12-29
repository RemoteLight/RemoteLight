package io.github.remotelight.core.output.config

import io.github.remotelight.core.config.Config
import io.github.remotelight.core.config.ConfigChangeCallback
import io.github.remotelight.core.config.property.Property
import io.github.remotelight.core.output.OutputDescriptor

class OutputConfig(
    configChangeCallback: ConfigChangeCallback,
    val outputDescriptor: OutputDescriptor
) : Config(configChangeCallback) {

    var name: String by Property("name", "")

    var pixels: Int by Property("pixels", 0)

}