package io.github.remotelight.core.config

import io.github.remotelight.core.config.loader.ConfigLoader
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.tinylog.kotlin.Logger
import kotlin.coroutines.CoroutineContext

abstract class Config: KoinComponent {

    private val propertyMap = HashMap<String, Property<*>>()
    private val coroutineContext: CoroutineContext by inject()
    private val scope = CoroutineScope(coroutineContext + Dispatchers.IO)

    init {
        loadProperties()
    }

    abstract fun getConfigLoader(): ConfigLoader

    fun loadProperties() {
        val properties = getConfigLoader().loadProperties()
        propertyMap.clear()
        propertyMap.putAll(properties.toPropertyMap())
        Logger.info("Loaded ${propertyMap.size} properties from ${getConfigLoader().getSource()}")
    }

    fun storeProperties() {
        getConfigLoader().storeProperties(propertyMap.toPropertyList())
        Logger.info("Stored ${propertyMap.size} properties in ${getConfigLoader().getSource()}")
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
        Logger.debug("Added property $property")
        val existing = propertyMap.put(property.id, property)
        notifyDataChanged()
        return (existing?: property) as Property<T>
    }

    fun removeProperty(id: String) = propertyMap.remove(id)

    fun hasProperty(id: String) = propertyMap.containsKey(id)


    /**
     * Waits for the job to complete if any is currently active.
     */
    fun cancelAndWait() {
        Logger.info("Closing config coroutine scope.")
        runBlocking {
            scope.coroutineContext[Job]?.apply {
                if(!isCompleted) {
                    cancelAndJoin()
                }
            }
        }
    }

    override fun toString(): String {
        return "Config(loader=${getConfigLoader()}, propertyMap=$propertyMap)"
    }

}