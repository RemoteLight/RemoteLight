package io.github.remotelight.core.config.loader

import io.github.remotelight.core.config.Property

interface ConfigLoader {

    fun loadProperties(): List<Property<*>>

    fun storeProperties(properties: List<Property<*>>)

    fun getSource(): String

}