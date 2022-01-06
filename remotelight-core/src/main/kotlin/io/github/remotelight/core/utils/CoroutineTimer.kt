package io.github.remotelight.core.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark
import kotlin.time.TimeSource

/**
 * Coroutine Timer inspired by [gmk57](https://gist.github.com/gmk57/67591e0c878cedc2a318c10b9d9f4c0c)
 */
class CoroutineTimer(
    var interval: Duration = Duration.ZERO,
    private val fpsUpdateRate: Double = 4.0
) {

    private var job: Job? = null

    private val _framesPerSecond = MutableSharedFlow<Double>(1, 0, BufferOverflow.DROP_OLDEST)
    val framesPerSecond = _framesPerSecond.asSharedFlow()

    @OptIn(ExperimentalTime::class)
    suspend fun start(scope: CoroutineScope, block: suspend () -> Unit) {
        job?.cancelAndJoin()
        job = scope.launch {
            val startTime = TimeSource.Monotonic.markNow()
            var count: Long = 0

            var frameCount = 0
            var deltaTime = 0.0
            var lastIterationMs = System.currentTimeMillis()

            while (isActive) {
                block()

                frameCount++
                deltaTime += (System.currentTimeMillis() - lastIterationMs) / 1000.0
                if (deltaTime > 1.0 / fpsUpdateRate) {
                    val fps = frameCount / deltaTime
                    _framesPerSecond.emit(fps)
                    frameCount = 0
                    deltaTime -= 1.0 / fpsUpdateRate
                }
                lastIterationMs = System.currentTimeMillis()

                // Long to Double conversion is generally lossy, but values up to 2^53 (285 million years
                // for 1-second intervals) will be represented exactly, see https://stackoverflow.com/a/1848762
                val nextTime = startTime + interval * (++count).toDouble()
                delay(nextTime.remaining())
            }
        }
    }

    suspend fun stopAndJoin() {
        job?.cancelAndJoin()
    }

    fun stop() {
        job?.cancel()
    }

    fun isRunning() = job.let { it != null && it.isActive }

    /** Returns the amount of time remaining until this mark (opposite of [TimeMark.elapsedNow]) */
    @ExperimentalTime
    private fun TimeMark.remaining(): Duration = -elapsedNow()

}
