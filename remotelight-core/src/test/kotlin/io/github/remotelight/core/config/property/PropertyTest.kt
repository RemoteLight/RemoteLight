package io.github.remotelight.core.config.property

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

internal class PropertyTest : BasePropertyTest() {

    @Test
    fun dataTest() {
        val property = Property("a", 0)
        assertEquals(0, property.defaultValue)
        assertEquals(0, property.getValue(testConfig))
        property.setValue(testConfig, 1)
        assertEquals(1, property.getValue(testConfig))
        property.setValue(testConfig, Int.MAX_VALUE)
        assertEquals(Int.MAX_VALUE, property.getValue(testConfig))
        testConfig.deleteProperty("a")
        assertEquals(0, property.getValue(testConfig))
    }

    @Test
    fun equalsTest() {
        val propA = Property("a", 1.0f)
        val propB = Property("b", 1.0f)
        assertNotEquals(propA, propB)

        val propA2 = Property("a", 1.0f)
        propA2.storeInConfig(testConfig)
        assertEquals(propA, propA2)
        assertEquals(propA.hashCode(), propA2.hashCode())
        propA.setValue(testConfig, 2.0f)
        assertTrue(propA.equals(propA2, testConfig))

        val propC = Property("c", "Test")
        val propC2 = Property("c", "Test")
        assertEquals(propC, propC2)
        propC2.setValue(testConfig, "test")
        assertTrue(propC.equals(propC2, testConfig))
    }

}