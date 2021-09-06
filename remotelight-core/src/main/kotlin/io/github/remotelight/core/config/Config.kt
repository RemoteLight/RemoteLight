package io.github.remotelight.core.config

import io.github.remotelight.core.config.loader.ConfigLoader
import io.github.remotelight.core.utils.reactive.Observer
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.tinylog.kotlin.Logger
import kotlin.coroutines.CoroutineContext

/**
 * A config class manages multiple properties.
 *
 * The inheriting class must specify a [ConfigLoader] with which the properties are loaded and saved.
 *
 * @sample GlobalConfig for a possible implementation
 */
abstract class Config: KoinComponent {

    private val propertyMap = HashMap<String, Property<*>>()
    private val propertyObservers = mutableListOf<Pair<Property<*>, Observer<*>>>()

    // global application coroutine context
    private val coroutineContext: CoroutineContext by inject()
    // coroutine scope is used for launching the store task in a separate coroutine
    private val scope = CoroutineScope(coroutineContext + Dispatchers.IO)

    /** Automatically store all properties on data change. */
    open val autoStoreOnChange: Boolean = true

    protected abstract fun createConfigLoader(): ConfigLoader

    val configLoader: ConfigLoader by lazy { createConfigLoader() }

    init {
        loadProperties()
    }

    /**
     * Loads the properties using the [ConfigLoader] specified by [createConfigLoader].
     * All previously contained properties will be removed.
     */
    @Synchronized
    fun loadProperties() {
        val properties = configLoader.loadProperties()
        propertyMap.clear()
        clearPropertyObservers()
        propertyMap.putAll(properties.toPropertyMap())
        addAllPropertyObservers()
        Logger.info("Loaded ${propertyMap.size} properties from ${configLoader.getSource()}")
    }

    @Synchronized
    fun storeProperties() {
        configLoader.storeProperties(propertyMap.toPropertyList())
        Logger.info("Stored ${propertyMap.size} properties in ${configLoader.getSource()}")
    }

    private fun HashMap<String, Property<*>>.toPropertyList(): List<Property<*>> {
        return this.map { entry -> entry.value }
    }

    private fun List<Property<*>>.toPropertyMap(): Map<String, Property<*>> {
        return this.associateBy { property -> property.id }
    }

    private fun notifyDataChanged() {
        if(!autoStoreOnChange) return
        scope.launch {
            storeProperties()
        }
    }


    private fun clearPropertyObservers() {
        propertyObservers.forEach { (prop, observer) -> prop.dataObservers.remove(observer as (Any?, Any?) -> Unit) }
        propertyObservers.clear()
    }

    private fun removePropertyObserver(property: Property<*>) {
        val pair = propertyObservers.find { it.first == property } ?: return
        propertyObservers.remove(pair)
        property.dataObservers.remove(pair.second as (Any?, Any?) -> Unit)
    }

    private fun addAllPropertyObservers() {
        propertyMap.forEach { addPropertyObserver(it.value) }
    }

    private fun addPropertyObserver(property: Property<*>) {
        val observer = property.observeBoth { oldValue, newValue ->
            if(oldValue != newValue) notifyDataChanged()
        }
        propertyObservers.add(Pair(property, observer))
    }


    fun <T> getProperty(id: String): Property<T>? = propertyMap[id] as? Property<T>?

    fun <T> getData(id: String, fallback: T? = null): T? = getProperty<T>(id)?.data?: fallback

    fun <T> addProperty(property: Property<T>): Property<T> {
        if(hasProperty(property.id)) {
            // return existing property and do not overwrite it
            return getProperty(property.id)?: property
        }
        // 'added' should be null since we have previously verified that the config does not have the property
        val added = propertyMap.put(property.id, property)
        addPropertyObserver(property)
        Logger.debug("Added property $property")
        notifyDataChanged()
        return (added?: property) as Property<T>
    }

    fun removeProperty(id: String): Property<*>? {
        val prev = propertyMap.remove(id) ?: return null
        removePropertyObserver(prev as Property<Any>)
        notifyDataChanged()
        return prev
    }

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
        return "Config(loader=${configLoader}, propertyMap=$propertyMap)"
    }

}