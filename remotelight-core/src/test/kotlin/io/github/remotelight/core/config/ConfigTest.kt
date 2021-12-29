package io.github.remotelight.core.config

import io.github.remotelight.core.config.property.Property
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

internal class ConfigTest : BaseConfigTest() {

    @Test
    fun propertyDelegate() {
        val testConfig = createTestConfig {
            object : Config(it) {
                var prop1: String by Property("prop.1", "")
                var prop2: String by Property("prop.2", "Property #2")
                val existing: String by Property("test", "-") // should not overwrite existing data
            }
        }

        assertNull(testConfig.getProperty("prop.1"))
        testConfig.prop1 = "a"
        assertEquals("a", testConfig.getProperty("prop.1"))
        assertNull(testConfig.getProperty("prop.2"))
        testConfig.prop2 = "b"
        assertEquals(testConfig.prop2, testConfig.getProperty("prop.2"))
        assertNotEquals("-", testConfig.getProperty("test"))
    }

    @Test
    fun addRemoveProperty() {
        val config = createTestConfig {
            object : Config(it) {
                var existing by Property("test", "default")
            }
        }
        assertEquals("last", config.existing)
        assertEquals("last", config.requirePropertyValue("test", String::class.java))

        val newProp = config.storeProperty("new", "default data")
        assertEquals("default data", newProp)
        assertEquals("default data", config.getProperty("new"))

        config.deleteProperty("new")
        assertFalse(config.hasProperty("new"))
        assertNull(config.getProperty("new"))

        config.existing = "new value"
        assertEquals("new value", config.existing)
        assertEquals("new value", config.getProperty("test"))
        config.deleteProperty("test")
        assertFalse(config.hasProperty("test"))
        assertEquals("default", config.existing)
    }

    @Test
    fun observeProperty() {
        val config = object : Config(EmptyConfigCallback()) {}
        assertFalse(config.hasProperty("test"))

        var observedValue: String? = null
        val observer = config.observeProperty<String?>("test") { oldValue, newValue ->
            assertEquals(observedValue, oldValue)
            observedValue = newValue
        }

        config.storeProperty("test", "a")
        assertEquals("a", observedValue)
        config.storeProperty("test", "b")
        assertEquals("b", observedValue)

        config.removeObserver("test", observer)
        config.storeProperty("test", "c")
        assertNotEquals("c", observedValue)
    }

}