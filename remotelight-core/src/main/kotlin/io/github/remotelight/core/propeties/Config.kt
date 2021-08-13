package io.github.remotelight.core.propeties

import io.github.remotelight.core.propeties.loader.ConfigLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

abstract class Config(private val loader: ConfigLoader): AutoCloseable {

    private val propertyMap = HashMap<String, Property<*>>()
    private val scope = CoroutineScope(Dispatchers.IO)

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

    private fun HashMap<String, Property<*>>.toPropertyList(): List<Property<*>> {
        return this.map { entry -> entry.value }
    }

    private fun List<Property<*>>.toPropertyMap(): Map<String, Property<*>> {
        return this.associateBy { property -> property.id }
    }

    private fun notifyDataChanged() {
        scope.launch {
            storeProperties()
        }
    }


    fun <T> getProperty(id: String): Property<T>? = propertyMap[id] as? Property<T>?

    fun <T> addProperty(property: Property<T>): Property<T> {
        val existing = propertyMap.put(property.id, property)
        notifyDataChanged()
        return (existing?: property) as Property<T>
    }

    fun removeProperty(id: String) = propertyMap.remove(id)

    fun hasProperty(id: String) = propertyMap.containsKey(id)


    /**
     * Cancels any running jobs of the coroutine scope.
     */
    override fun close() {
        scope.cancel()
    }

}