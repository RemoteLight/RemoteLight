package io.github.remotelight.core.propeties

import io.github.remotelight.core.propeties.loader.ConfigLoader

abstract class Configuration(private val loader: ConfigLoader) {

    private val propertyMap = HashMap<String, Property>()

    init {
        loadProperties()
    }

    fun loadProperties() {
        val properties = loader.loadProperties()
        propertyMap.clear()
        propertyMap.putAll(properties.toPropertyMap())
    }

    fun storeProperties() {
        loader.storeProperties(propertyMap.toPropertyList())
    }

    private fun HashMap<String, Property>.toPropertyList(): List<Property> {
        return this.map { entry -> entry.value }
    }

    private fun List<Property>.toPropertyMap(): Map<String, Property> {
        return this.associateBy { property -> property.id }
    }

}