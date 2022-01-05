package io.github.remotelight.core.color

import kotlin.math.pow

object GammaCorrection {

    private val internGammaTable by lazy { createGammaTable() }

    val gammaTable get() = internGammaTable.clone()

    private fun createGammaTable(gamma: Float = 2.8f): Array<UByte> {
        return Array(256) { i ->
            val f = (i / 255.0f).pow(gamma)
            (f * 255 + 0.5f).toInt().toUByte()
        }
    }

    operator fun get(index: Int) = gammaTable[index]

    fun applyGammaCorrection(color: Color): Color {
        return Color(
            r = gammaTable[color.red].toInt(),
            g = gammaTable[color.green].toInt(),
            b = gammaTable[color.blue].toInt(),
            w = gammaTable[color.white].toInt()
        )
    }

}