package io.github.remotelight.core.config

import io.github.remotelight.core.config.loader.ConfigLoader
import io.github.remotelight.core.constants.Defaults
import io.github.remotelight.core.utils.CoroutineDebounce
import io.github.remotelight.core.utils.Debounce
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.tinylog.kotlin.Logger

open class Config(
    private val configLoader: ConfigLoader,
    loadPropertiesOnInit: Boolean = true
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    internal open val storeDebounce: Debounce<Unit> = CoroutineDebounce(Defaults.CONFIG_STORE_DEBOUNCE_DELAY, scope)

    private val properties = mutableMapOf<String, Any?>()

    init {
        if (loadPropertiesOnInit) {
            loadPropertyValues()
        }
    }

    fun <T> storeProperty(id: String, value: T): T {
        val oldValue = properties.put(id, value)
        onPropertyChanged(id, oldValue, value)
        return value
    }

    fun hasProperty(id: String) = properties.contains(id)

    fun getProperty(id: String) = properties[id]

    fun <T> requirePropertyValue(id: String, type: Class<out T>): T {
        val value = getProperty(id)
        if (value != null && type.isAssignableFrom(value::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return value as T
        }
        throw IllegalArgumentException("Property with ID $id and type ${value?.javaClass?.name} is not compatible with type ${type.name}")
    }

    fun deleteProperty(id: String) {
        val oldValue = properties.remove(id)
        onPropertyDeleted(id, oldValue)
    }

    private fun onPropertyChanged(id: String, oldValue: Any?, newValue: Any?) {
        Logger.trace("Property changed ($id): $oldValue -> $newValue")
        storePropertyValues()
    }

    private fun onPropertyDeleted(id: String, oldValue: Any?) {
        Logger.trace("Property deleted ($id): $oldValue")
        storePropertyValues()
    }

    @Synchronized
    private fun storePropertyValues() {
        storeDebounce.debounce {
            Logger.debug("Storing ${properties.size} property values to ${configLoader.getSource()}...")
            val wrapper = PropertyValuesWrapper(properties = properties)
            configLoader.storePropertyValues(wrapper)
            Logger.debug("Successfully stored property values.")
        }
    }

    @Synchronized
    private fun loadPropertyValues() {
        Logger.info("Loading property values from ${configLoader.getSource()}...")
        val wrapper = configLoader.loadPropertyValues()
        if (wrapper != null) {
            properties.putAll(wrapper.properties)
            Logger.info("Successfully loaded ${wrapper.properties.size} property values.")
        } else {
            Logger.info("No property values available.")
        }
    }

    fun cancelScope() {
        scope.cancel()
    }

    override fun toString(): String {
        return "NewConfig(configValueLoader=$configLoader, storeDebounce=$storeDebounce, properties=$properties)"
    }

}

inline fun <reified T> Config.getPropertyValue(id: String): T? {
    val value = getProperty(id)
    if (value is T) {
        return value
    }
    return null
}

inline fun <reified T> Config.requirePropertyValue(id: String): T {
    val value = getProperty(id)
    if (value is T) {
        return value
    }
    throw IllegalArgumentException("Property with ID $id and type ${value?.javaClass?.name} is not compatible with type ${T::class.java.name}")
}
