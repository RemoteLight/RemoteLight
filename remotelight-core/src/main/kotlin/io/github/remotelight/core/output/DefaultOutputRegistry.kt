package io.github.remotelight.core.output

import io.github.remotelight.core.output.config.OutputConfig
import io.github.remotelight.core.output.impl.SerialOutput
import io.github.remotelight.core.output.protocol.AdalightProtocol
import io.github.remotelight.core.output.protocol.GlediatorProtocol
import io.github.remotelight.core.output.protocol.TPM2Protocol

object DefaultOutputRegistry : OutputRegistry {

    private val outputFactories = mutableMapOf<OutputIdentifier, OutputFactory<*>>()

    override fun createOutput(outputIdentifier: OutputIdentifier, outputConfig: OutputConfig): Output? {
        return outputFactories[outputIdentifier]?.createOutput(outputConfig)
    }

    override fun getRegisteredOutputTypes(): List<OutputIdentifier> = outputFactories.keys.toList()

    override fun registerOutput(outputIdentifier: OutputIdentifier, factory: OutputFactory<*>) {
        if (outputFactories.containsKey(outputIdentifier)) {
            throw IllegalArgumentException("OutputFactory for the output type '$outputIdentifier' is already registered.")
        }
        outputFactories[outputIdentifier] = factory
    }

    init {
        initDefaultOutputFactories()
    }

    private fun initDefaultOutputFactories() {
        registerOutput("serial_glediator") { SerialOutput(it, GlediatorProtocol) }
        registerOutput("serial_adalight") { SerialOutput(it, AdalightProtocol) }
        registerOutput("serial_tpm2") { SerialOutput(it, TPM2Protocol) }
    }

}