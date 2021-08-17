package io.github.remotelight.core.function

import io.github.remotelight.core.error.ErrorMessage

sealed class Status {
    object Running: Status()
    object Stopped: Status()
    data class Failed(val errorMessage: ErrorMessage): Status()
}
