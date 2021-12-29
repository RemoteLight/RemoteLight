package io.github.remotelight.core.error

data class ErrorMessage(
    val message: String,
    val throwable: Throwable? = null
)