package io.github.remotelight.core.effect.runner

import io.github.remotelight.core.effect.Effect
import io.github.remotelight.core.effect.StripPainter
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.tinylog.kotlin.Logger
import java.util.*

class CoroutineEffectRunner(
    superVisorJob: Job,
    override var delay: Long
) : EffectRunner {

    private val scope = CoroutineScope(Dispatchers.Unconfined + superVisorJob)

    private var runnerJob: Job? = null

    private var timer: Timer? = null

    private val _framesPerSecond = MutableSharedFlow<Double>(1, 0, BufferOverflow.DROP_OLDEST)
    override val framesPerSecond = _framesPerSecond.asSharedFlow()

    override suspend fun start(task: EffectRunnerTask) {
        runnerJob?.cancelAndJoin()
        timer?.cancel()
        runnerJob = scope.launch {
            val effect = task.effect
            Logger.trace("Running effect task $task.")
            effect.onEnable(task.pixels)
            try {
                val updateRate = 8.0
                var frameCount = 0
                var deltaTime = 0.0
                var lastIterationMs = System.currentTimeMillis()
                while (isActive) {
                    val stripPainter = task.stripPainterFactory.createStripPainter(task)
                    executeEffect(effect, stripPainter)

                    frameCount++
                    deltaTime += (System.currentTimeMillis() - lastIterationMs) / 1000.0
                    if (deltaTime > 1.0 / updateRate) {
                        val fps = frameCount / deltaTime
                        _framesPerSecond.emit(fps)
                        frameCount = 0
                        deltaTime -= 1.0 / updateRate
                    }

                    lastIterationMs = System.currentTimeMillis()
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
        timer?.cancel()
    }

    override fun isRunning(): Boolean {
        return runnerJob.let { it != null && it.isActive }
    }

}