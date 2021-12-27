package io.github.remotelight.core.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

interface Debounce<T> {
    val delay: Long
    fun debounce(function: () -> T)
}

class CoroutineDebounce<T>(
    override val delay: Long,
    private val coroutineScope: CoroutineScope
) : Debounce<T> {
    private var debounceJob: Job? = null

    override fun debounce(function: () -> T) {
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            delay(delay)
            function()
        }
    }
}
