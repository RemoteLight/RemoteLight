package io.github.remotelight.core.effect.runner

import io.github.remotelight.core.player.PlaybackContent

interface EffectRunner : PlaybackContent {

    var delay: Long

    suspend fun start(task: EffectRunnerTask)

    suspend fun stop()

    fun isRunning(): Boolean

}