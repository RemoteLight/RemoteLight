package io.github.remotelight.core.propeties

import io.github.remotelight.core.propeties.loader.ConfigLoader
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class ConfigTest {

    @Test
    fun propertyDelegate() {
        val testConfig = object : Config(TestConfigLoader()) {
            val prop1: String by Property("prop.1", "")
            val prop2: String by Property("prop.2", "Property #2")
            val existing: String by Property("test", "-") // should not overwrite existing data
        }

        assertNotNull(testConfig.getProperty<String>("prop.1"))
        assertEquals("", testConfig.getProperty<String>("prop.1")?.data)
        assertNotNull(testConfig.getProperty<String>("prop.2"))
        assertEquals(testConfig.prop2, testConfig.getProperty<String>("prop.2")?.data)
        assertNotEquals("-", testConfig.getProperty<String>("test")?.data)
    }

}

internal class TestConfigLoader: ConfigLoader {
    private var properties: List<Property<*>> = MutableList(5) { i -> Property("test_$i", getRandomValue()) } + Property("test", "last")

    override fun loadProperties(): List<Property<*>> = properties

    override fun storeProperties(properties: List<Property<*>>) {
        this.properties = properties
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