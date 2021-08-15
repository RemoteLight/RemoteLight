package io.github.remotelight.core.config.wrapper

import io.github.remotelight.core.config.Property
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class RangedPropertyTest {

    @Test
    fun rangedProperty() {
        val property = Property("test", 1)
        val rangedProp = RangedProperty(property, -1..10)
        assertEquals(property, rangedProp.property)
        assertEquals(property.data, rangedProp.data)
        assertEquals(-1..10, rangedProp.range)

        property.data = 10
        assertEquals(10, property.data)
        rangedProp.data = 8
        assertEquals(8, property.data)
        property.data = 11
        assertEquals(10, property.data)
        property.data = -1
        assertEquals(-1, rangedProp.data)
        property.data = -2
        assertEquals(-1, property.data)

        rangedProp.range = 0..10
        assertEquals(0, property.data)
    }

    @Test
    fun doubleRange() {
        val property = Property("test", 25.25001)
        assertEquals(25.25001, property.data)
        val rangedProp = RangedProperty(property, -1.5..25.25)
        assertEquals(25.25, property.data)

        rangedProp.data += 1.0 / 1000.0
        assertEquals(25.25, property.data)
        property.data /= 5
        assertEquals(5.05, property.data)
        property.data -= 10
        assertEquals(-1.5, property.data)

        rangedProp.range = -5.5..10.5
        assertEquals(-1.5, property.data)
        property.data -= 4
        assertEquals(-5.5, property.data)
    }

}