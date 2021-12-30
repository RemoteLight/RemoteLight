package io.github.remotelight.core.config

import io.github.remotelight.core.utils.reactive.Observer
import io.github.remotelight.core.utils.reactive.ObserverList
import org.tinylog.kotlin.Logger

open class Config(
    var configChangeCallback: ConfigChangeCallback? = null
) : PropertyHolder {

    var isDestroyed = false
        private set

    private val properties = mutableMapOf<String, Any?>()

    private val propertyObserver = mutableMapOf<String, ObserverList<Any?>>()

    override fun <T : Any?> storeProperty(id: String, value: T): T {
        requireNotDestroyed()
        val oldValue = properties.put(id, value)
        onPropertyChanged(id, oldValue, value)
        return value
    }

    fun hasProperty(id: String) = requireNotDestroyed { properties.contains(id) }

    override fun getProperty(id: String) = requireNotDestroyed { properties[id] }

    fun <T> requirePropertyValue(id: String, type: Class<out T>): T {
        requireNotDestroyed()
        val value = getProperty(id)
        if (value != null && type.isAssignableFrom(value::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return value as T
        }
        throw IllegalArgumentException("Property with ID $id and type ${value?.javaClass?.name} is not compatible with type ${type.name}")
    }

    fun deleteProperty(id: String) {
        requireNotDestroyed()
        val oldValue = properties.remove(id)
        onPropertyDeleted(id, oldValue)
    }

    private fun onPropertyChanged(id: String, oldValue: Any?, newValue: Any?) {
        Logger.trace("Property changed ($id): $oldValue -> $newValue")
        configChangeCallback?.onConfigChange(properties)
        propertyObserver[id]?.notify(oldValue, newValue)
    }

    private fun onPropertyDeleted(id: String, oldValue: Any?) {
        Logger.trace("Property deleted ($id): $oldValue")
        configChangeCallback?.onConfigChange(properties)
        propertyObserver[id]?.notify(oldValue, null)
    }

    @Synchronized
    fun setPropertyValues(properties: Map<String, Any?>, clear: Boolean = false) {
        requireNotDestroyed()
        if (clear) {
            this.properties.clear()
        }
        this.properties.putAll(properties)
    }

    fun getPropertyValues() = properties.toMap()

    @Suppress("UNCHECKED_CAST")
    fun <T : Any?> observeProperty(id: String, observer: Observer<T>): Observer<T> {
        requireNotDestroyed()
        return propertyObserver[id]?.observe(observer as Observer<Any?>) ?: with(ObserverList<Any?>()) {
            propertyObserver[id] = this
            observe(observer as Observer<Any?>)
        }
    }

    fun <T : Any?> observeProperty(id: String, observer: (newValue: T) -> Unit): Observer<T> {
        return observeProperty(id) { _, newValue -> observer(newValue) }
    }

    fun removeObserver(id: String, observer: Observer<*>) {
        requireNotDestroyed()
        @Suppress("UNCHECKED_CAST")
        propertyObserver[id]?.remove(observer as Observer<Any?>)
    }

    /**
     * Destroy this config instance by removing all observers and callbacks and clearing all property values.
     * Make sure to store the property values before calling [destroy].
     *
     * This config instance cannot be used after calling [destroy].
     */
    fun destroy() {
        configChangeCallback = null
        propertyObserver.forEach { it.value.clear() }
        propertyObserver.clear()
        properties.clear()
        isDestroyed = true
    }

    private fun requireNotDestroyed() {
        if (isDestroyed) {
            throw IllegalStateException("This config was destroyed and cannot be re-used!")
        }
    }

    private fun <T> requireNotDestroyed(block: () -> T): T {
        requireNotDestroyed()
        return block()
    }

    override fun toString(): String {
        return "Config(properties=$properties, propertyObserver=$propertyObserver)"
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
