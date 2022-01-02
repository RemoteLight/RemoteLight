package io.github.remotelight.core.tools

import io.github.remotelight.core.utils.Debounce

internal class NoDelayDebounce<T> : Debounce<T> {
    override val delay: Long = 0
    override fun debounce(function: () -> T) {
        function()
    }
}