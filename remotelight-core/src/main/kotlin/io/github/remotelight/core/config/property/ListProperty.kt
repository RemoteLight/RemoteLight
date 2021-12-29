package io.github.remotelight.core.config.property

import io.github.remotelight.core.config.PropertyHolder

class ListProperty<T>(
    id: String,
    defaultValue: T,
    vararg elements: T
) : Property<T>(id, defaultValue), MutableCollection<T> {

    private val collection = elements.toMutableList()

    init {
        if (!collection.contains(defaultValue)) {
            throw IllegalArgumentException("The default value ($defaultValue) is not contained in the specified collection ($collection).")
        }
    }

    override fun setValue(propertyHolder: PropertyHolder, value: T): T {
        val previous = getValue(propertyHolder)
        return super.setValue(propertyHolder, ensureInList(value, previous))
    }

    private fun ensureInList(value: T, previous: T? = null): T {
        return if (!collection.contains(value) && previous != null) {
            previous
        } else if (!collection.contains(value)) {
            defaultValue
        } else {
            value
        }
    }

    override val size: Int
        get() = collection.size

    override fun contains(element: T): Boolean = collection.contains(element)

    override fun containsAll(elements: Collection<T>): Boolean = collection.containsAll(elements)

    override fun isEmpty(): Boolean = collection.isEmpty()

    override fun add(element: T): Boolean = collection.add(element)

    override fun addAll(elements: Collection<T>): Boolean = collection.addAll(elements)

    override fun clear() = collection.clear()

    override fun iterator(): MutableIterator<T> = collection.iterator()

    override fun remove(element: T): Boolean = collection.remove(element)

    override fun removeAll(elements: Collection<T>): Boolean = collection.removeAll(elements)

    override fun retainAll(elements: Collection<T>): Boolean = collection.retainAll(elements)

    override fun toString(): String {
        return "ListProperty(collection=$collection) ${super.toString()}"
    }

}