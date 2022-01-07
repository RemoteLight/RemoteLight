package io.github.remotelight.core.config

import io.github.remotelight.core.config.provider.PropertyProvider
import io.github.remotelight.core.error.UnknownPropertyException
import io.github.remotelight.core.utils.reactive.Observer
import io.github.remotelight.core.utils.reactive.ObserverList
import org.tinylog.kotlin.Logger

open class Config(
    private val propertyProvider: PropertyProvider<*>
) : PropertyHolder {

    init {
        propertyProvider.onInit()
    }

    override val storeDefaultValue: Boolean = false

    var isDestroyed = false
        private set

    private val propertiesCache = mutableMapOf<String, Any?>()

    private val propertyObserver = mutableMapOf<String, ObserverList<Any?>>()

    override fun <T : Any?> storeProperty(id: String, value: T): T {
        requireNotDestroyed()
        val oldValue = propertiesCache.put(id, value)
        propertyProvider.setProperty(id, value)
        onPropertyChanged(id, oldValue, value)
        return value
    }

    fun hasProperty(id: String): Boolean {
        requireNotDestroyed()
        return propertiesCache.contains(id) || propertyProvider.hasProperty(id)
    }

    override fun <T : Any?> getProperty(id: String, type: Class<T>): T? {
        requireNotDestroyed()
        return try {
            propertyProvider.getProperty(id, type).also { value ->
                propertiesCache[id] = value
            }
        } catch (e: Exception) {
            Logger.error(e, "Failed to get property for ID '$id'.")
            null
        }
    }

    inline fun <reified T> getProperty(id: String): T? {
        return getProperty(id, T::class.java)
    }

    fun deleteProperty(id: String) {
        requireNotDestroyed()
        propertiesCache.remove(id)
        val oldValue = propertyProvider.deleteProperty(id)
        onPropertyDeleted(id, oldValue)
    }

    private fun onPropertyChanged(id: String, oldValue: Any?, newValue: Any?) {
        Logger.trace("Property changed ($id): $oldValue -> $newValue")
        propertyProvider.storeProperties()
        propertyObserver[id]?.notify(oldValue, newValue)
    }

    private fun onPropertyDeleted(id: String, oldValue: Any?) {
        Logger.trace("Property deleted ($id): $oldValue")
        propertyProvider.storeProperties()
        propertyObserver[id]?.notify(oldValue, null)
    }

    fun getProperties() = propertyProvider.getProperties() + getResolvedProperties()

    fun getRawProperties() = propertyProvider.getRawProperties()

    /**
     * Get all properties resolved using this config instance.
     * Use [getProperties] to get all properties from the property values provider.
     */
    fun getResolvedProperties() = propertiesCache.toMap()

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
     * Destroy this config instance by removing all observers and clearing all property values.
     * Make sure to store the property values before calling [destroy].
     *
     * This config instance cannot be used after calling [destroy].
     */
    fun destroy() {
        propertyProvider.clear()
        propertyProvider.onClose()
        propertyObserver.forEach { it.value.clear() }
        propertyObserver.clear()
        propertiesCache.clear()
        isDestroyed = true
    }

    private fun requireNotDestroyed() {
        if (isDestroyed) {
            throw IllegalStateException("This config was destroyed and cannot be re-used!")
        }
    }

    override fun toString(): String {
        return "Config(propertiesCache=$propertiesCache, propertyObserver=$propertyObserver)"
    }

}

inline fun <reified T> Config.requireProperty(id: String): T {
    return getProperty<T>(id) ?: throw UnknownPropertyException("Property with ID '$id' not found.")
}
