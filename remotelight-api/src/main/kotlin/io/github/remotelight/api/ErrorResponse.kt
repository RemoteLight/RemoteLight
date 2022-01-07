package io.github.remotelight.api

data class ErrorResponse(
    val exception: Class<out Throwable>?,
    val message: String?
) {

    constructor(t: Throwable) : this(t::class.java, t.message)

    constructor(message: String) : this(null, message)

}