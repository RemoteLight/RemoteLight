package io.github.remotelight.core.config.property

import io.github.remotelight.core.config.Config
import io.github.remotelight.core.config.PropertyHolder
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class Property<T : Any?>(
    val id: String,
    val defaultValue: T
) {

    open fun getValue(propertyHolder: PropertyHolder, type: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return propertyHolder.getProperty(id, type) ?: defaultValue
    }

    open fun setValue(propertyHolder: PropertyHolder, value: T) = propertyHolder.storeProperty(id, value)

    override fun toString(): String {
        return "Property(id='$id', defaultValue=$defaultValue)"
    }

}

inline fun <reified T> Property<T>.getValue(propertyHolder: PropertyHolder) = getValue(propertyHolder, T::class.java)

inline fun <reified T> PropertyHolder.property(id: String, defaultValue: T): ReadWriteProperty<Any?, T> {
    return object : ReadWriteProperty<Any?, T> {
        init {
            if (storeDefaultValue) {
                storeProperty(id, defaultValue)
            }
        }

        override fun getValue(thisRef: Any?, property: KProperty<*>) = getProperty(id, T::class.java) ?: defaultValue

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            storeProperty(id, value)
        }
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
inline fun <reified T> Property<T>.withConfig(config: Config): ReadWriteProperty<Any?, T> =
    object : ReadWriteProperty<Any?, T> {
        init {
            if (config.storeDefaultValue) {
                config.storeProperty(id, defaultValue)
            }
        }

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return this@withConfig.getValue(config, T::class.java)
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            this@withConfig.setValue(config, value)
        }
    }
