package io.github.remotelight.core.config.loader

import io.github.remotelight.core.config.PropertyValuesWrapper
import io.github.remotelight.core.io.Loader

interface ConfigLoader : Loader {

    fun loadPropertyValues(): PropertyValuesWrapper?

    fun storePropertyValues(valuesWrapper: PropertyValuesWrapper)

}