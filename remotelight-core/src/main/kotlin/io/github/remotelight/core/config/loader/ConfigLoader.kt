package io.github.remotelight.core.config.loader

import io.github.remotelight.core.config.Property
import io.github.remotelight.core.io.Loader

interface ConfigLoader: Loader {

    fun loadProperties(): List<Property<*>>

    fun storeProperties(properties: List<Property<*>>)

}