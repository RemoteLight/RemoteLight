package io.github.remotelight.core.error

open class ErrorMessage(open val message: String, open val throwable: Throwable? = null)