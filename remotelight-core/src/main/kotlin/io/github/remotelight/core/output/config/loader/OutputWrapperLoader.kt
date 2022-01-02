package io.github.remotelight.core.output.config.loader

import io.github.remotelight.core.io.Loader
import io.github.remotelight.core.output.config.OutputConfigWrapper

interface OutputWrapperLoader<T> : Loader {

    fun loadOutputWrappers(): List<OutputConfigWrapper<T>>?

    fun storeOutputWrappers(outputWrappers: List<OutputConfigWrapper<T>>)

}