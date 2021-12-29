package io.github.remotelight.core.config.property

import io.github.remotelight.core.config.Config
import io.github.remotelight.core.config.PropertyHolder
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class Property<T : Any?>(
    val id: String,
    val defaultValue: T
) : ReadWriteProperty<PropertyHolder, T> {

    open fun getValue(propertyHolder: PropertyHolder): T {
        @Suppress("UNCHECKED_CAST")
        return (propertyHolder.getProperty(id) as? T) ?: defaultValue
    }

    open fun setValue(propertyHolder: PropertyHolder, value: T) = propertyHolder.storeProperty(id, value)

    override fun getValue(thisRef: PropertyHolder, property: KProperty<*>): T {
        return getValue(thisRef)
    }

    override fun setValue(thisRef: PropertyHolder, property: KProperty<*>, value: T) {
        setValue(thisRef, value)
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
 * Stores the default value of the property in the given property holder.
 */
fun <T> Property<T>.storeInConfig(propertyHolder: PropertyHolder): T {
    return propertyHolder.storeProperty(id, defaultValue)
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
