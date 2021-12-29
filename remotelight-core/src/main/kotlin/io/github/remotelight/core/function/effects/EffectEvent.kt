package io.github.remotelight.core.function.effects

data class EffectEvent<T : Effect>(val eventType: EventType, val effect: T) {

    enum class EventType {
        Add, Remove, Start, Stop
    }

}
