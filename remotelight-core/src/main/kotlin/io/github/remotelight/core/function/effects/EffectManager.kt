package io.github.remotelight.core.function.effects

import io.github.remotelight.core.function.FunctionManager
import io.github.remotelight.core.utils.reactive.Listener
import io.github.remotelight.core.utils.reactive.ListenerList

/**
 * Listen only to the specified event types. If no event type is specified, the listener will be notified for
 * all event types.
 */
fun <T: Effect> EffectManager<T>.observeEvent(vararg eventTypes: EffectEvent.EventType, listener: (EffectEvent<T>) -> Unit): Listener<EffectEvent<T>> {
    val wrapper = this.eventListeners.listen { event ->
        if(eventTypes.contains(event.eventType) || eventTypes.isEmpty()) {
            listener(event)
        }
    }
    return wrapper
}

abstract class EffectManager<T : Effect>: FunctionManager<T>() {

    val eventListeners = ListenerList<EffectEvent<T>>()
    protected val effects: MutableSet<T> = mutableSetOf()

    fun getAll() = mutableSetOf(effects)

    fun add(effect: T) = effects.add(effect).also { added ->
        if (added) eventListeners.notify(EffectEvent(EffectEvent.EventType.Add, effect))
    }

    fun remove(effect: T) = effects.remove(effect).also { removed ->
        if (removed) eventListeners.notify(EffectEvent(EffectEvent.EventType.Remove, effect))
    }

}