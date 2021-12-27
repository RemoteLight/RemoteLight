package io.github.remotelight.core.config.loader

import io.github.remotelight.core.config.PropertyValuesWrapper
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class JsonConfigLoaderTest {

    /* Must match the number of properties defined in `/resources/test_config.json` */
    private val testFilePropertiesLength = 6

    @Test
    fun loadProperties() {
        val testFilePath = javaClass.classLoader.getResource("test_config.json")?.file ?: "test_config.json"
        val file = File(testFilePath)

        println("Test file location: ${file.absolutePath}")
        assumeTrue(file.isFile, "Test file does not exist! Looking for '${file.absolutePath}'.")

        val configLoader = JsonConfigLoader(file)
        val wrapper = configLoader.loadPropertyValues()
        val properties = wrapper?.properties
        assertEquals(testFilePropertiesLength, properties?.size)
    }

    @Test
    fun storeProperties() {
        val file = File("build/resources/test", "test_config_stored.json")
        println("Test file location: ${file.absolutePath}")

        val testProperties = buildMap<String, Any?> {
            for (i in 0..5) {
                put("test_$i", if (i % 2 == 0) i else "$i")
            }
        }
        val configLoader = JsonConfigLoader(file)
        configLoader.storePropertyValues(PropertyValuesWrapper(properties = testProperties))
        assertTrue(file.isFile)

        // load properties from file to validate stored data
        val loadedWrapper = configLoader.loadPropertyValues()
        val loadedProperties = loadedWrapper?.properties
        assertNotNull(loadedProperties)
        assertEquals(testProperties.size, loadedProperties.size)
        assertEquals(testProperties, loadedProperties)
    }

}