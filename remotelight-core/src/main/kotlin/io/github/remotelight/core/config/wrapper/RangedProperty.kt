package io.github.remotelight.core.config.wrapper

import io.github.remotelight.core.config.Property
import io.github.remotelight.core.config.observe

class RangedProperty<T: Comparable<T>>(property: Property<T>, range: ClosedRange<T>): PropertyWrapper<T>(property) {

    var range = range
        set(value) {
            field = value
            ensureInRange()
        }

    init {
        ensureInRange()
        property.observe { ensureInRange() }
    }

    private fun ensureInRange() {
        if(property.data !in range) {
            property.data = property.data.coerceIn(range)
        }
    }

}