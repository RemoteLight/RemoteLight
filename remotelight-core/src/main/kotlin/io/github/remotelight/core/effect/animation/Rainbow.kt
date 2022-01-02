package io.github.remotelight.core.effect.animation

import com.github.ajalt.colormath.model.HSV
import io.github.remotelight.core.color.Color
import io.github.remotelight.core.color.convert
import io.github.remotelight.core.config.property.property
import io.github.remotelight.core.effect.Effect
import io.github.remotelight.core.effect.EffectConfig
import io.github.remotelight.core.effect.EffectMode
import io.github.remotelight.core.effect.StripPainter
import kotlin.math.sin

class Rainbow(config: EffectConfig) : Effect(config) {

    enum class Mode(override val displayName: String) : EffectMode {
        HSV("HSV"),
        SineWave("SineWave"),
        SineBow("SineBow")
    }

    var mode by property("mode", Mode.HSV)
    var static by property("static", false)
    var resolution by property("resolution", 10)

    private var step = 0f
    private var increment = 0.001f

    override fun doEffect(strip: StripPainter) {
        increment = resolution * 0.001f
        when (mode) {
            Mode.HSV -> hsvRainbow(strip)
            Mode.SineWave -> sineWaveRainbow(strip)
            Mode.SineBow -> sineBowRainbow(strip)
        }
    }

    private fun hsvRainbow(strip: StripPainter) {
        val color = HSV(step * 255f, 1f, 1f).convert()
        if (static) {
            strip.all(color)
        } else {
            strip.shiftRight()
            strip[0] = color
        }
        step += increment
        if (step > 1.0f) {
            step = 0.0f
        }
    }

    private fun sineWaveRainbow(strip: StripPainter) {
        if (static) {
            strip.all(sineWaveFunction(0))
        } else {
            for (i in 0 until strip.size) {
                strip[i] = sineWaveFunction(i)
            }
        }
        step += 0.1f
        if (step > (Float.MAX_VALUE - 2 * increment)) {
            step = 0.0f
        }
    }

    private fun sineWaveFunction(i: Int): Color {
        val frequency = increment * 10.0f
        val r = sin(frequency * i + 0 - step) * 127f + 128f
        val g = sin(frequency * i + 2 - step) * 127f + 128f
        val b = sin(frequency * i + 4 - step) * 127f + 128f
        return Color(r.toInt(), g.toInt(), b.toInt())
    }

    private fun sineBowRainbow(strip: StripPainter) {
        if (static) {
            strip.all(sineBowFunction(0))
        } else {
            for (i in 0 until strip.size) {
                strip[i] = sineBowFunction(i)
            }
        }
        step += 0.1f
        if (step > (Float.MAX_VALUE - 1.0f)) {
            step = 0.0f
        }
    }

    private fun sineBowFunction(i: Int): Color {
        val res = 2.0 + increment
        val r = sin(res * Math.PI * i - step) * 0.5 + 0.5
        val g = sin(res * Math.PI * (i + 1.0 / 3.0) - step) * 0.5 + 0.5
        val b = sin(res * Math.PI * (i + 2.0 / 3.0) - step) * 0.5 + 0.5

        val red = (r * 255.0).toInt()
        val green = (g * 255.0).toInt()
        val blue = (b * 255.0).toInt()
        return Color(red, green, blue)
    }

}