package io.github.remotelight.core.output

import io.github.remotelight.core.output.config.OutputConfig
import io.github.remotelight.core.output.impl.SerialOutput
import io.github.remotelight.core.output.protocol.GlediatorProtocol

object OutputRegistry {

    private val outputFactories = mutableMapOf<OutputIdentifier, OutputFactory<*>>()

    fun createOutput(outputIdentifier: OutputIdentifier, outputConfig: OutputConfig): Output? {
        return outputFactories[outputIdentifier]?.createOutput(outputConfig)
    }

    fun registerOutput(outputIdentifier: OutputIdentifier, factory: OutputFactory<*>) {
        if (outputFactories.containsKey(outputIdentifier)) {
            throw IllegalArgumentException("OutputFactory for the output type '$outputIdentifier' is already registered.")
        }
        outputFactories[outputIdentifier] = factory
    }

    init {
        initDefaultOutputFactories()
    }

    private fun initDefaultOutputFactories() {
        registerOutput("serial_glediator") { outputConfig ->
            SerialOutput(outputConfig, GlediatorProtocol)
        }
    }

}