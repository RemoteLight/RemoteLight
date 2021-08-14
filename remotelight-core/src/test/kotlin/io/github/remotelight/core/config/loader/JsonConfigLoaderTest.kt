package io.github.remotelight.core.config.loader

import io.github.remotelight.core.config.Property
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test
import java.io.File

internal class JsonConfigLoaderTest {

    private val testFilePropertiesLength = 5

    @Test
    fun loadProperties() {
        val testFileUrl = javaClass.classLoader.getResource("test_config.json")?.file?: "test_config.json"
        val file = File(testFileUrl)
        println("Test file location: ${file.absolutePath}")
        assumeTrue(file.isFile, "Test file does not exist! Looking for '${file.absolutePath}'.")
        val configLoader = JsonConfigLoader(file)
        val properties = configLoader.loadProperties()
        assertEquals(testFilePropertiesLength, properties.size)
    }

    @Test
    fun storeProperties() {
        val file = File("build/resources/test", "test_config_stored.json")
        println("Test file location: ${file.absolutePath}")
        val testProperties = List(5) { i -> Property("test_$i", if(i % 2 == 0) i else "$i") }
        val configLoader = JsonConfigLoader(file)
        configLoader.storeProperties(testProperties)
        assertTrue(file.isFile)
        // load properties from file to validate stored data
        val loadedProperties = configLoader.loadProperties()
        assertEquals(testProperties.size, loadedProperties.size)
        assertEquals(testProperties, loadedProperties)
    }

}