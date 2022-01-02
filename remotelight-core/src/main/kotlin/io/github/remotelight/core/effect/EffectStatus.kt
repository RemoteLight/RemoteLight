package io.github.remotelight.core.effect

import io.github.remotelight.core.error.ErrorMessage

sealed class EffectStatus {
    object Running : EffectStatus()
    object Stopped : EffectStatus()
    data class Failed(val errorMessage: ErrorMessage) : EffectStatus()
}
