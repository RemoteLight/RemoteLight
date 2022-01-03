package io.github.remotelight.core.effect.runner

import io.github.remotelight.core.effect.StripPainter

fun interface StripPainterFactory {

    fun createStripPainter(runnerTask: EffectRunnerTask): StripPainter

}