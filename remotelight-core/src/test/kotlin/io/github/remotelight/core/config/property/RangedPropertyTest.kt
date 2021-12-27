package io.github.remotelight.core.config.property

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

internal class RangedPropertyTest : BasePropertyTest() {

    @Test
    fun rangedProperty() {
        val rangedProp = RangedProperty("test", 1, -1..10)
        assertEquals(1, rangedProp.defaultValue)
        assertEquals(-1..10, rangedProp.range)

        rangedProp.setValue(testConfig, 8)
        assertEquals(8, rangedProp.getValue(testConfig))
        rangedProp.setValue(testConfig, -1)
        assertEquals(-1, rangedProp.getValue(testConfig))
        rangedProp.setValue(testConfig, 11)
        assertEquals(10, rangedProp.getValue(testConfig))
        rangedProp.setValue(testConfig, -2)
        assertEquals(-1, rangedProp.getValue(testConfig))
    }

    @Test
    fun doubleRange() {
        assertThrows<IllegalArgumentException> { RangedProperty("ranged", 25.25001, -1.5..25.25) }
        val rangedProp = RangedProperty("ranged", 25.25, -1.5..25.25)
        assertEquals(25.25, rangedProp.defaultValue)

        var value by rangedProp.withConfig(testConfig)

        value += 1.0 / 1000.0
        assertEquals(25.25, value)
        assertEquals(25.25, rangedProp.getValue(testConfig))
        value /= 5
        assertEquals(5.05, rangedProp.getValue(testConfig))
        value -= 10
        assertEquals(-1.5, rangedProp.getValue(testConfig))
    }

}