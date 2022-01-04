package io.github.remotelight.core.output.protocol

import io.github.remotelight.core.color.Color

/**
 * Details about the TPM2 protocol can be found [here](https://gist.github.com/jblang/89e24e2655be6c463c56) (english)
 * or [here](https://www.ledstyles.de/index.php?thread/18969-tpm2-protokoll-zur-matrix-lichtsteuerung) (german).
 *
 * [TPM2 V1.0 Specification](https://www.ledstyles.de/content/index.php?attachment/28200-tpm2-specs-v1-0-2013-ger-pdf) (german)
 *
 * Byte | Value
 * ---- | -----
 * 0    | 0xC9
 * 1    | Packet type: 0xDA (Data frame), 0xC0 (Command) or 0xAA (Requested response)
 * 2    | Payload size, high byte
 * 3    | Payload size, low byte
 * 4-n  | Data (1 - 65,535 bytes)
 * n+1  | Packet end byte 0x36
 */
object TPM2Protocol : PixelProtocol {

    override fun processPixels(pixels: Array<Color>, specs: PixelProtocolSpecs): ByteArray {
        val colorChannels = if (specs.isRGBW) 4 else 3
        val payloadSize = pixels.size * colorChannels

        val outputBuffer = ByteArray(5 + payloadSize)

        outputBuffer[0] = 0xC9.toByte()
        outputBuffer[1] = 0xDA.toByte()
        outputBuffer[2] = (payloadSize shr 8).toByte()
        outputBuffer[3] = (payloadSize and 0xff).toByte()

        var index = 4
        pixels.forEach { color ->
            outputBuffer[index + 0] = color.red.toByte()
            outputBuffer[index + 1] = color.green.toByte()
            outputBuffer[index + 2] = color.blue.toByte()
            if (specs.isRGBW) {
                outputBuffer[index + 3] = color.white.toByte()
            }
            index += colorChannels
        }

        outputBuffer[index] = 0x36.toByte()

        return outputBuffer
    }

    override fun supportsRGBW(): Boolean = true

}