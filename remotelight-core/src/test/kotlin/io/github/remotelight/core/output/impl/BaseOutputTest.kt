package io.github.remotelight.core.output.impl

import io.github.remotelight.core.color.Color
import io.github.remotelight.core.config.ConfigChangeCallback
import io.github.remotelight.core.output.config.OutputConfig

internal abstract class BaseOutputTest : ConfigChangeCallback {

    fun testOutputConfig(pixels: Int = 60) = OutputConfig(this, "test_output").apply {
        this.pixels = pixels
    }

    override fun onConfigChange(properties: Map<String, Any?>) {}

    fun generatePixelArray(pixels: Int, color: Color) = Array(pixels) { color }

}