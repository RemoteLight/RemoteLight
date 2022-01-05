package io.github.remotelight.core.output.config

import io.github.remotelight.core.config.Config
import io.github.remotelight.core.config.property.property
import io.github.remotelight.core.config.provider.PropertyProvider
import io.github.remotelight.core.output.ColorOrder
import io.github.remotelight.core.output.OutputIdentifier

class OutputConfig(
    propertyProvider: PropertyProvider<*>,
    val outputIdentifier: OutputIdentifier,
    val id: String
) : Config(propertyProvider) {

    var name: String by property("name", "")

    var pixels: Int by property("pixels", 0)

    var colorOrder: ColorOrder by property("color_order", ColorOrder.RGB)

    var gammaCorrectionEnabled: Boolean by property("gamma_correction", false)

}