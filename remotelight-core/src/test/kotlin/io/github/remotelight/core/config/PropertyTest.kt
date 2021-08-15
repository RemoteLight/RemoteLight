package io.github.remotelight.core.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertNotEquals

internal class PropertyTest {

    @Test
    fun observeProperty() {
        val property = Property("test", "initial")
        assertEquals("initial", property.data)
        // set value to this variable first before updating the property in order that the Observer can be tested
        var newValue = "test1"
        val observer = property.observe {
            assertEquals(newValue, it)
        }
        assertEquals(1, property.dataObservers.size)

        property.data = newValue
        newValue = "test2"
        property.data = newValue

        property.dataObservers.remove(observer)
        assertEquals(0, property.dataObservers.size)
    }

    @Test
    fun dataTest() {
        val property = Property("test", 0)
        assertEquals(0, property.data)
        assertEquals(1, ++property.data)
        property.data = Int.MAX_VALUE
        assertEquals(Int.MAX_VALUE, property.data)
    }

    @Test
    fun equalsTest() {
        val propA = Property("a", 1.0f)
        val propB = Property("b", 1.0f)
        assertNotEquals(propA, propB)
        val propA2 = Property("a", 1.0f)
        assertEquals(propA, propA2)
        assertEquals(propA.hashCode(), propA2.hashCode())
        propA2.data += 1.0f
        assertNotEquals(propA, propA2)

        val propC = Property("c", "Test")
        val propC2 = Property("c", "Test")
        assertEquals(propC, propC2)
        propC2.data = "test"
        assertNotEquals(propC, propC2)
    }

}