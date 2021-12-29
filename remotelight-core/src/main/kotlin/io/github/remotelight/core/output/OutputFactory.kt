package io.github.remotelight.core.output

import io.github.remotelight.core.output.config.OutputConfig

fun interface OutputFactory<T : Output> {

    fun createOutput(outputConfig: OutputConfig): T

}