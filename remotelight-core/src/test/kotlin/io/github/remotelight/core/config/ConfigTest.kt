package io.github.remotelight.core.config

import io.github.remotelight.core.config.loader.ConfigLoader
import io.github.remotelight.core.config.property.Property
import io.github.remotelight.core.di.configModule
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.junit5.AutoCloseKoinTest
import org.koin.test.junit5.KoinTestExtension
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

internal class ConfigTest : AutoCloseKoinTest() {

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(configModule)
    }

    @Test
    fun propertyDelegate() {
        val testConfig = object : Config(TestConfigLoader()) {
            var prop1: String by Property("prop.1", "")
            var prop2: String by Property("prop.2", "Property #2")
            val existing: String by Property("test", "-") // should not overwrite existing data
        }

        assertNull(testConfig.getProperty("prop.1"))
        testConfig.prop1 = "a"
        assertEquals("a", testConfig.getProperty("prop.1"))
        assertNull(testConfig.getProperty("prop.2"))
        testConfig.prop2 = "b"
        assertEquals(testConfig.prop2, testConfig.getProperty("prop.2"))
        assertNotEquals("-", testConfig.getProperty("test"))

        testConfig.cancelScope()
    }

    @Test
    fun addRemoveProperty() {
        val config = object : Config(TestConfigLoader()) {
            var existing by Property("test", "default")
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
    fun debounceTest() {
        val loader = object : ConfigLoader {
            var storeCounter = 0
            override fun loadPropertyValues(): PropertyValuesWrapper? = null
            override fun storePropertyValues(valuesWrapper: PropertyValuesWrapper) {
                storeCounter++
                println("Stored property values #$storeCounter")
            }
            override fun getSource() = "Test Loader"
        }
        val config = object : Config(loader) {}

        for (i in 0..10) {
            config.storeProperty("$i", "test value #$i")
        }
        runBlocking {
            delay(config.storeDebounce.delay + 10)
            assertEquals(1, loader.storeCounter)
        }
    }

}

internal class TestConfigLoader : ConfigLoader {
    private val properties: MutableMap<String, Any?> = generateList()

    override fun loadPropertyValues(): PropertyValuesWrapper = PropertyValuesWrapper(properties = properties)

    override fun storePropertyValues(valuesWrapper: PropertyValuesWrapper) {
        this.properties.clear()
        this.properties.putAll(valuesWrapper.properties)
    }

    override fun getSource() = "Test-Data"

    private fun generateList(): MutableMap<String, Any?> {
        val map = buildMap<String, Any?>(5) {
            for (i in 0..5) {
                put("test_$i", getRandomValue())
            }
        }.toMutableMap()
        map["test"] = "last"
        return map
    }

    private fun getRandomValue(): Any {
        return when (Random.nextInt(4)) {
            0 -> "Test String ${Random.nextLong()}"
            1 -> (System.currentTimeMillis() % 2L) == 0L
            2 -> Random.nextDouble()
            else -> Random.nextInt()
        }
    }
}