package io.github.remotelight.core.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

interface Debounce<T> {
    val delay: Long
    fun debounce(function: () -> T)
}

/**
 * Runs the function after the delay is reached.
 * Subsequent calls before the end of the delay is reached will reset the timer.
 */
open class CoroutineDebounce<T>(
    override val delay: Long,
    protected val coroutineScope: CoroutineScope
) : Debounce<T> {
    protected var debounceJob: Job? = null

    override fun debounce(function: () -> T) {
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            delay(delay)
            function()
        }
    }
}

/**
 * First call will run the function immediately.
 * The last function of subsequent calls will after the cool-down/delay is reached.
 */
class CoolDownDebounce<T>(
    delay: Long,
    coroutineScope: CoroutineScope
) : CoroutineDebounce<T>(delay, coroutineScope) {
    private var lastFinished = -1L

    override fun debounce(function: () -> T) {
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            if (lastFinished > 0) {
                val remaining = delay - (System.currentTimeMillis() - lastFinished)
                delay(remaining)
            }
            function()
            lastFinished = System.currentTimeMillis()
        }
    }
}
