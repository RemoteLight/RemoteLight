package io.github.remotelight.core.utils.reactive

/**
 * List for holding listeners which get notified on a certain event.
 */
class ListenerList<T> {

    private val listeners = mutableListOf<Listener<T>>()

    fun listen(listener: Listener<T>): Listener<T> {
        listeners.add(listener)
        return listener
    }

    fun remove(listener: Listener<T>) = listeners.remove(listener)

    fun clear() = listeners.clear()

    val size get() = listeners.size

    fun notify(event: T) = listeners.forEach { it.onEvent(event) }

}

fun interface Listener<T> {
    fun onEvent(event: T)
}