package io.github.remotelight.core.output

import io.github.remotelight.core.output.config.OutputConfig

object OutputRegistry {

    private val outputFactories = mutableMapOf<OutputIdentifier, OutputFactory<*>>()

    fun createOutput(outputIdentifier: OutputIdentifier, outputConfig: OutputConfig): Output? {
        return outputFactories[outputIdentifier]?.createOutput(outputConfig)
    }

    fun registerOutputFactory(outputIdentifier: OutputIdentifier, factory: OutputFactory<*>) {
        if (outputFactories.containsKey(outputIdentifier)) {
            throw IllegalArgumentException("OutputFactory for the output type '$outputIdentifier' is already registered.")
        }
        outputFactories[outputIdentifier] = factory
    }

}