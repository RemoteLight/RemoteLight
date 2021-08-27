package io.github.remotelight.core.config

import io.github.remotelight.core.config.loader.ConfigLoader
import io.github.remotelight.core.di.Modules
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
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
            override fun createConfigLoader() = TestConfigLoader()

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
            override fun createConfigLoader() = TestConfigLoader()
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

    @Test
    fun propertyObservation() = runBlocking {
        val loader = TestConfigLoader()
        val config = object : Config() {
            override fun createConfigLoader() = loader
        }

        val property = config.addProperty(Property("text", "test value"))
        assertEquals(1, property.dataObservers.size)
        delay(10) // wait for the store action to finish
        assertTrue(loader.properties.contains(property))
        property.data = "updated"
        delay(10)
        assertEquals(property.data, loader.properties.find { it == property }?.data)
        config.removeProperty(property.id)
        delay(10)
        assertFalse(loader.properties.contains(property))
        assertEquals(0, property.dataObservers.size)
    }

}

internal class TestConfigLoader: ConfigLoader {
    val properties: MutableList<Property<*>> = generateList()

    override fun loadProperties(): List<Property<*>> = properties

    override fun storeProperties(properties: List<Property<*>>) {
        this.properties.clear()
        this.properties.addAll(properties)
    }

    override fun getSource() = "Test-Data"

    private fun generateList(): MutableList<Property<*>> {
        val list = MutableList<Property<*>>(5) { i -> Property("test_$i", getRandomValue()) }
        list.add(Property("test", "last"))
        return list
    }

    private fun getRandomValue(): Any {
        return when(Random.nextInt(4)) {
            0 -> "Test String ${Random.nextLong()}"
            1 -> (System.currentTimeMillis() % 2L) == 0L
            2 -> Random.nextDouble()
            else -> Random.nextInt()
        }
    }
}