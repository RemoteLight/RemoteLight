package io.github.remotelight.core.effect.runner

import kotlinx.coroutines.flow.SharedFlow

interface EffectRunner {

    var delay: Long

    /** Reports the calculated frames per second. */
    val framesPerSecond: SharedFlow<Double>

    suspend fun start(task: EffectRunnerTask)

    suspend fun stop()

    fun isRunning(): Boolean

}