package io.github.remotelight.core.config.property

import io.github.remotelight.core.config.Config
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class Property<T : Any?>(
    val id: String,
    val defaultValue: T
) : ReadWriteProperty<Config, T> {

    open fun getValue(config: Config): T {
        @Suppress("UNCHECKED_CAST")
        return (config.getProperty(id) as? T) ?: defaultValue
    }

    open fun setValue(config: Config, value: T) = config.storeProperty(id, value)

    override fun getValue(thisRef: Config, property: KProperty<*>): T {
        @Suppress("UNCHECKED_CAST")
        return (thisRef.getProperty(id) as? T) ?: defaultValue
    }

    override fun setValue(thisRef: Config, property: KProperty<*>, value: T) {
        thisRef.storeProperty(id, value)
    }

    fun equals(other: Property<*>, config: Config): Boolean {
        return this == other && this.getValue(config) == other.getValue(config)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Property<*>) return false

        if (id != other.id) return false
        if (defaultValue != other.defaultValue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (defaultValue?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Property(id='$id', defaultValue=$defaultValue)"
    }

}

/**
 * Stores the default value of the property in the given config.
 */
fun <T> Property<T>.storeInConfig(config: Config): T {
    return config.storeProperty(id, defaultValue)
}

/**
 * Delegate the property value using the given config.
 * ```kotlin
 * val myProperty = Property("id", "value")
 * var myValue by myProperty.withConfig(config)
 * ```
 */
fun <T> Property<T>.withConfig(config: Config): ReadWriteProperty<Any?, T> =
    object : ReadWriteProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return this@withConfig.getValue(config, property)
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            this@withConfig.setValue(config, property, value)
        }
    }
