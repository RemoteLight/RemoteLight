package io.github.remotelight.core.propeties.loader

import io.github.remotelight.core.propeties.Property

interface ConfigLoader {

    fun loadProperties(): List<Property<*>>

    fun storeProperties(properties: List<Property<*>>)

}