package io.github.remotelight.core.output.protocol

import io.github.remotelight.core.color.Color

object GlediatorProtocol : PixelProtocol {

    override fun processPixels(pixels: Array<Color>): ByteArray {
        val outputBuffer = ByteArray(pixels.size * 3 + 1)

        var index = 0
        outputBuffer[0] = 1
        index++

        pixels.forEach { pixel ->
            val r = pixel.red.colorComponent()
            val g = pixel.green.colorComponent()
            val b = pixel.blue.colorComponent()

            outputBuffer[index + 0] = r
            outputBuffer[index + 1] = g
            outputBuffer[index + 2] = b

            index += 3
        }

        return outputBuffer
    }

    private fun Int.colorComponent(): Byte {
        return if (this == 1) 2 else this.toByte()
    }

    // TODO: port glediator protocol to RGBW
    override fun supportsRGBW(): Boolean = false

}