package io.github.remotelight.core.effect

import io.github.remotelight.core.color.Color
import java.util.*

class StripPainter(val buffer: Array<Color>) {

    constructor(pixels: Int, initialColor: Color = Color.BLACK) : this(Array(pixels) { initialColor })

    val size get() = buffer.size

    operator fun get(index: Int) = buffer[index]

    operator fun set(index: Int, color: Color) = apply {
        buffer[index] = color
    }

    fun setAll(vararg color: Color) = apply {
        color.forEachIndexed { index, color ->
            buffer[index] = color
        }
    }

    fun all(color: Color) = apply {
        for (i in buffer.indices) {
            this[i] = color
        }
    }

    fun last() = buffer.last()

    fun first() = buffer.first()

    fun rotate(distance: Int) = apply {
        val list = buffer.toList()
        Collections.rotate(list, distance)
        setAll(*list.toTypedArray())
    }

    fun shiftRight(amount: Int = 1) = rotate(amount)

    fun shiftLeft(amount: Int = 1) = rotate(-amount)

}