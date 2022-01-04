package io.github.remotelight.core.output.protocol

import io.github.remotelight.core.color.Color
import kotlin.experimental.xor

/**
 * [Adalight LEDstream protocol](https://forums.adafruit.com/viewtopic.php?f=47&t=29970#p150824)
 *
 * Byte | Value
 * ---- | -----
 * 0    | 'A' (0x41)
 * 1    | 'd' (0x64)
 * 2    | 'a' (0x61)
 * 3    | LED count, high byte
 * 4    | LED count, low byte
 * 5    | Checksum (high byte XOR low byte XOR 0x55)
 * 6    | red (0..255)
 * 7    | green (0..255)
 * 8    | blue (0..255)
 * ...  | Repeat bytes 6 to 8 for each further LED
 */
object AdalightProtocol : PixelProtocol {

    override fun processPixels(pixels: Array<Color>): ByteArray {
        val outputBuffer = ByteArray(6 + pixels.size * 3)

        outputBuffer[0] = 'A'.code.toByte()
        outputBuffer[1] = 'd'.code.toByte()
        outputBuffer[2] = 'a'.code.toByte()
        outputBuffer[3] = ((pixels.size - 1) shr 8).toByte()
        outputBuffer[4] = ((pixels.size - 1) and 0xff).toByte()
        outputBuffer[5] = (outputBuffer[3] xor outputBuffer[4] xor 0x55)

        var index = 6
        pixels.forEach { color ->
            outputBuffer[index + 0] = color.red.toByte()
            outputBuffer[index + 1] = color.green.toByte()
            outputBuffer[index + 2] = color.blue.toByte()
            index += 3
        }

        return outputBuffer
    }

    override fun supportsRGBW(): Boolean = false

}