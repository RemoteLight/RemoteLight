package io.github.remotelight.core.config.property

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

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

}