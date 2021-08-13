package io.github.remotelight.core.propeties

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class PropertyTest {

    @Test
    fun observeProperty() {
        val property = Property("test", "initial")
        assertEquals("initial", property.data)
        // set value to this variable first before updating the property in order that the Observer can be tested
        var newValue = "test1"
        val observer: (Any?) -> Unit = {
            println("changed to $it")
            assertEquals(newValue, it)
        }
        property.dataObservers.add(observer)
        assertEquals(1, property.dataObservers.size)

        property.data = newValue
        newValue = "test2"
        property.data = newValue

        property.dataObservers.remove(observer)
        assertEquals(0, property.dataObservers.size)
    }

}