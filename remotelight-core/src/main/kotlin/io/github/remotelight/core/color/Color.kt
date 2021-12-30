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
    ) : this(((w.cap() and 0xFF) shl 24) or ((r.cap() and 0xFF) shl 16) or ((g.cap() and 0xFF) shl 8) or ((b.cap() and 0xFF) shl 0))

    val white get() = (rgbw shr 24) and 0xFF

    val red get() = (rgbw shr 16) and 0xFF

    val green get() = (rgbw shr 8) and 0xFF

    val blue get() = (rgbw shr 0) and 0xFF

    companion object {
        val RED get() = Color(255, 0, 0)
        val GREEN get() = Color(0, 255, 0)
        val BLUE get() = Color(0, 0, 255)
        val BLACK get() = Color(0, 0, 0)
    }

}

private fun Int.cap() = this.coerceIn(0..255)