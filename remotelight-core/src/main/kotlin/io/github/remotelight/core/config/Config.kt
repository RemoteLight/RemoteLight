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

    fun <T> getData(id: String, fallback: T? = null): T? = getProperty<T>(id)?.data?: fallback

    fun <T> addProperty(property: Property<T>): Property<T> {
        if(hasProperty(property.id)) {
            // return existing property and do not overwrite it
            return getProperty<T>(property.id)?: property
        }
        val added = propertyMap.put(property.id, property)
        Logger.debug("Added property $property")
        notifyDataChanged()
        return (added?: property) as Property<T>
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