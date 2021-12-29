package io.github.remotelight.core.output

sealed class OutputStatus {
    object Connected : OutputStatus()
    object Connecting : OutputStatus()
    object Disconnected : OutputStatus()
    object Disconnecting : OutputStatus()
    data class NotAvailable(val reason: String) : OutputStatus()
    data class Failed(val throwable: Throwable) : OutputStatus()
}
