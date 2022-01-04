package io.github.remotelight.core.output.protocol

import io.github.remotelight.core.color.Color

/**
 * Sends a '1' at the beginning of each packet followed by the red, green and blue values for each LED.
 *
 * Since the '1' is used as magic word, 1's are mapped to 2's in the RGB data.
 * Therefore, only 254 brightness levels per color channel are possible.
 *
 * Byte | Value
 * ---- | -----
 * 0    | '1'
 * 1    | red (0..255)
 * 2    | green (0..255)
 * 3    | blue (0..255)
 * ...  | repeat bytes 1 to 3 for each further LED
 */
object GlediatorProtocol : PixelProtocol {

    override fun processPixels(pixels: Array<Color>, specs: PixelProtocolSpecs): ByteArray {
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