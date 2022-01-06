package io.github.remotelight.core.effect.runner

import io.github.remotelight.core.color.Color
import io.github.remotelight.core.color.ColorBase
import io.github.remotelight.core.effect.Effect
import io.github.remotelight.core.effect.StripPainter
import kotlinx.coroutines.*
import org.tinylog.kotlin.Logger

class CoroutineEffectRunner(
    override var delay: Long,
    override val playbackPriority: Int = 100
) : EffectRunner {

    private var playbackScope: CoroutineScope? = null

    private var runnerJob: Job? = null

    private var pixelBuffer: Array<Color>? = null

    override fun hasContent(): Boolean = !pixelBuffer.isNullOrEmpty()

    override fun produce(): Array<out ColorBase>? {
        return pixelBuffer
    }

    override fun getDescription(): String ="Effect Runner"

    override fun onPlayerEnable(playbackScope: CoroutineScope) {
        this.playbackScope = playbackScope
    }

    override fun onPlayerDisable() {
        runnerJob?.cancel()
        playbackScope = null
    }

    override suspend fun start(task: EffectRunnerTask) {
        stop()
        val scope = playbackScope ?: throw IllegalStateException("Playback Scope unavailable. Is the player running?")
        runnerJob = scope.launch {
            val effect = task.effect
            Logger.trace("Running effect task $task.")
            effect.onEnable(task.pixels)
            try {
                while (isActive) {
                    val stripPainter = task.stripPainterFactory.createStripPainter(task)
                    executeEffect(effect, stripPainter)

                    delay(delay)
                }
            } finally {
                effect.onDisable()
                Logger.trace("Finished effect task $task.")
            }
        }
    }

    @Synchronized
    private fun executeEffect(effect: Effect, stripPainter: StripPainter) {
        effect.doEffect(stripPainter)
    }

    override suspend fun stop() {
        runnerJob?.cancelAndJoin()
    }

    override fun isRunning(): Boolean {
        return runnerJob.let { it != null && it.isActive }
    }

}