package io.github.remotelight.core.config.wrapper

import io.github.remotelight.core.config.Property

abstract class PropertyWrapper<T>(val property: Property<T>) {

    var data
        get() = property.data
        set(value) { property.data = value }

}