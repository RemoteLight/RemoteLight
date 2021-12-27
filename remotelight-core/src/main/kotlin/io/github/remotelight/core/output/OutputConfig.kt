package io.github.remotelight.core.output

import io.github.remotelight.core.config.Config
import io.github.remotelight.core.config.loader.ConfigLoader
import io.github.remotelight.core.config.property.Property

class OutputConfig(loader: ConfigLoader, val outputClassName: String): Config(loader) {

    val name: String by Property("name", "")

    val pixels: Int by Property("pixels", 0)

}