package io.github.remotelight.core.effect.runner

import io.github.remotelight.core.effect.Effect

data class EffectRunnerTask(
    val effect: Effect,
    val pixels: Int,
    val stripPainterFactory: StripPainterFactory
)