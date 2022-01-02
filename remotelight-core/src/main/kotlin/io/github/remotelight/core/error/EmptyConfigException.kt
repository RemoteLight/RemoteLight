package io.github.remotelight.core.error

class EmptyConfigException : Exception {

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)

}