package io.github.remotelight.core.output

sealed class OutputVerification {
    object Ok : OutputVerification()
    data class MissingProperty(val properties: List<String>) : OutputVerification() {
        constructor(vararg properties: String) : this(properties.toList())
    }
    data class NotAvailable(val reason: String?) : OutputVerification()
}
