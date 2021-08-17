package io.github.remotelight.core.config

import io.github.remotelight.core.config.loader.ConfigLoader
import io.github.remotelight.core.di.Modules
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.junit5.AutoCloseKoinTest
import org.koin.test.junit5.KoinTestExtension
import kotlin.random.Random

internal class ConfigTest: AutoCloseKoinTest() {

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(Modules.coreModule)
    }

    @Test
    fun propertyDelegate() {
        val testConfig = object : Config() {
            override fun getConfigLoader() = TestConfigLoader()

            val prop1: String by Property("prop.1", "")
            val prop2: String by Property("prop.2", "Property #2")
            val existing: String by Property("test", "-") // should not overwrite existing data
        }

        assertNotNull(testConfig.getProperty<String>("prop.1"))
        assertEquals("", testConfig.getProperty<String>("prop.1")?.data)
        assertNotNull(testConfig.getProperty<String>("prop.2"))
        assertEquals(testConfig.prop2, testConfig.getProperty<String>("prop.2")?.data)
        assertNotEquals("-", testConfig.getProperty<String>("test")?.data)

        testConfig.cancelAndWait()
    }

    @Test
    fun addRemoveProperty() {
        val config = object : Config() {
            override fun getConfigLoader() = TestConfigLoader()
        }
        val existingProp = config.addProperty(Property("test", "default"))
        assertEquals("last", existingProp.data)
        assertEquals("last", config.getProperty<String>("test")?.data)
        val newProp = config.addProperty(Property("new", "default data"))
        assertEquals("default data", newProp.data)
        assertEquals("default data", config.getProperty<String>("new")?.data)
        assertEquals("fallback data", config.getData("not.exist", "fallback data"))
        assertEquals("default data", config.getData("new", "fallback"))

        val removedNew = config.removeProperty(newProp.id)
        assertEquals(newProp, removedNew)
        assertFalse(config.hasProperty(newProp.id))
        assertNull(config.getProperty<String>(newProp.id))
        assertEquals("fallback", config.getData(newProp.id, "fallback"))
    }

}

internal class TestConfigLoader: ConfigLoader {
    private var properties: List<Property<*>> = MutableList(5) { i -> Property("test_$i", getRandomValue()) } + Property("test", "last")

    override fun loadProperties(): List<Property<*>> = properties

    override fun storeProperties(properties: List<Property<*>>) {
        this.properties = properties
    }

    override fun getSource() = "Test-Data"

    private fun getRandomValue(): Any {
        return when(Random.nextInt(4)) {
            0 -> "Test String ${Random.nextLong()}"
            1 -> (System.currentTimeMillis() % 2L) == 0L
            2 -> Random.nextDouble()
            else -> Random.nextInt()
        }
    }
}