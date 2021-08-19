package io.github.remotelight.core.utils.reactive

typealias Observer<T> = (oldValue: T, newValue: T) -> Unit

/**
 * List for holding observers which get notified about a value change.
 */
class ObserverList<T> {

    private val observers = mutableListOf<Observer<T>>()

    fun observe(observer: Observer<T>): Observer<T> {
        observers.add(observer)
        return observer
    }

    fun observe(observer: (newValue: T) -> Unit): Observer<T> {
        return observe { _, newValue -> observer(newValue) }
    }

    fun remove(observer: Observer<T>) = observers.remove(observer)

    fun clear() = observers.clear()

    val size get() = observers.size

    fun notify(oldValue: T, newValue: T) = observers.forEach { it(oldValue, newValue) }

}
