package io.github.remotelight.core.output

import io.github.remotelight.core.output.config.OutputConfig

interface OutputRegistry {

    fun createOutput(outputIdentifier: OutputIdentifier, outputConfig: OutputConfig): Output?

    fun getRegisteredOutputTypes(): List<OutputIdentifier>

    fun registerOutput(outputIdentifier: OutputIdentifier, factory: OutputFactory<*>)

}