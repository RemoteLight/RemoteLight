package io.github.remotelight.core.color

/**
 * Color class that stores a RGBW color in a single Integer (32 bits).
 * Blue is stored in bits 0-7, Green in bits 8-15, Red in bits 16-23
 * and White in bits 24-31.
 *
 * ```
 * <MSB                                  LSB>
 * 0000 0000  0000 0000  0000 0000  0000 0000
 * ----W----  ----R----  ----G----  ----B----
 * ```
 */
data class Color(val rgbw: Int) {

    constructor(
        r: Int,
        g: Int,
        b: Int,
        w: Int = 0
    ) : this(((w and 0xFF) shl 24) or ((r and 0xFF) shl 16) or ((g and 0xFF) shl 8) or ((b and 0xFF) shl 0))

    val white get() = (rgbw shr 24) and 0xFF

    val red get() = (rgbw shr 16) and 0xFF

    val green get() = (rgbw shr 8) and 0xFF

    val blue get() = (rgbw shr 0) and 0xFF

}