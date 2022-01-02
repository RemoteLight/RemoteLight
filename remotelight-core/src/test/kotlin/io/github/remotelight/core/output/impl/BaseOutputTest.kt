package io.github.remotelight.core.output.impl

import io.github.remotelight.core.color.Color
import io.github.remotelight.core.config.BaseConfigTest
import io.github.remotelight.core.output.config.OutputConfig
import java.util.*

internal abstract class BaseOutputTest {

    fun testOutputConfig(pixels: Int = 60) = OutputConfig(
        BaseConfigTest.TestPropertyProvider(0),
        "test_output",
        UUID.randomUUID().toString()
    ).apply {
        this.pixels = pixels
    }

    fun generatePixelArray(pixels: Int, color: Color) = Array(pixels) { color }

}