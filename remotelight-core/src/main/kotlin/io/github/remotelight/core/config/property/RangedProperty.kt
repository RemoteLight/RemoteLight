package io.github.remotelight.core.config.property

import io.github.remotelight.core.config.Config
import kotlin.reflect.KProperty

class RangedProperty<T : Comparable<T>>(
    id: String,
    defaultValue: T,
    val range: ClosedRange<T>
) : Property<T>(id, defaultValue) {

    init {
        if (ensureInRange(defaultValue) != defaultValue) {
            throw IllegalArgumentException("The default value ($defaultValue) is not within the specified range ($range).")
        }
    }

    override fun setValue(config: Config, value: T) = super.setValue(config, ensureInRange(value))

    override fun setValue(thisRef: Config, property: KProperty<*>, value: T) {
        super.setValue(thisRef, property, ensureInRange(value))
    }

    private fun ensureInRange(value: T): T {
        return if (value !in range) {
            value.coerceIn(range)
        } else {
            value
        }
    }

    override fun toString(): String {
        return "RangedProperty(range=$range) ${super.toString()}"
    }

}