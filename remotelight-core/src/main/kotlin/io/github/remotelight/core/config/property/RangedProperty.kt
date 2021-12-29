package io.github.remotelight.core.config.property

import io.github.remotelight.core.config.PropertyHolder

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

    override fun setValue(propertyHolder: PropertyHolder, value: T) = super.setValue(propertyHolder, ensureInRange(value))

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