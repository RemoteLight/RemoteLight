package io.github.remotelight.core.function.effects

import io.github.remotelight.core.function.FunctionManager

typealias Observer<T> = (EffectEvent<T>) -> Unit

abstract class EffectManager<T : Effect>: FunctionManager<T>() {

    val eventObserver = mutableListOf<Observer<T>>()
    protected val effects: MutableSet<T> = mutableSetOf()

    fun getAll() = mutableSetOf(effects)

    fun add(effect: T) = effects.add(effect).also {
        if (it) fireEvent(EffectEvent(EffectEvent.EventType.Add, effect))
    }

    fun remove(effect: T) = effects.remove(effect).also {
        if (it) fireEvent(EffectEvent(EffectEvent.EventType.Remove, effect))
    }

    fun observe(observer: Observer<T>, vararg eventTypes: EffectEvent.EventType): Observer<T> {
        val obs: Observer<T> = if (eventTypes.isEmpty()) {
            observer
        } else { event ->
            if (eventTypes.contains(event.eventType)) {
                observer(event)
            }
        }
        this.eventObserver.add(obs)
        return obs
    }

    protected fun fireEvent(event: EffectEvent<T>) {
        eventObserver.forEach { it(event) }
    }

}