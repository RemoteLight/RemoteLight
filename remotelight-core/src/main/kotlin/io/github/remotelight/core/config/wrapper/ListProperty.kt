package io.github.remotelight.core.config.wrapper

import io.github.remotelight.core.config.Property
import io.github.remotelight.core.config.observeBoth

class ListProperty<T>(property: Property<T>, vararg elements: T): PropertyWrapper<T>(property) {

    private val collection = elements.toMutableList()

    init {
        ensureInList()
        // if the property gets a new value assigned that is not in the collection,
        // its value will be reset to the old value
        property.observeBoth { old, new ->
            if(!collection.contains(new)) {
                property.data = old
            }
        }
    }

    private fun ensureInList() {
        if(!collection.contains(property.data) && collection.size > 0) {
            property.data = collection.first()
        }
    }

    fun getElements() = collection.toList()

    fun add(element: T) = collection.add(element)

    fun add(index: Int, element: T) = collection.add(index, element)

    fun addAll(elements: Collection<T>) = collection.addAll(elements)

    fun addAll(index: Int, elements: Collection<T>) = collection.addAll(index, elements)

    fun remove(element: T) = collection.remove(element).also { ensureInList() }

    fun removeAt(index: Int) = collection.removeAt(index).also { ensureInList() }

    fun clear() = collection.clear()

    fun size() = collection.size

    fun contains(element: T) = collection.contains(element)

}