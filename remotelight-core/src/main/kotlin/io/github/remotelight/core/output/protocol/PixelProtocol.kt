package io.github.remotelight.core.output.protocol

import io.github.remotelight.core.color.Color

interface PixelProtocol {

    fun processPixels(pixels: Array<Color>): ByteArray

    fun supportsRGBW(): Boolean

}