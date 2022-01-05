package io.github.remotelight.core.output.config.loader

import com.fasterxml.jackson.databind.JsonNode
import io.github.remotelight.core.config.loader.JsonPropertySource
import io.github.remotelight.core.config.provider.JsonPropertyProvider
import io.github.remotelight.core.di.configModule
import io.github.remotelight.core.output.ColorOrder
import io.github.remotelight.core.output.config.OutputConfig
import io.github.remotelight.core.output.config.OutputConfigWrapper
import io.github.remotelight.core.tools.NoDelayDebounce
import io.github.remotelight.core.utils.Debounce
import kotlinx.coroutines.CoroutineScope
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.core.component.get
import org.koin.dsl.module
import org.koin.test.junit5.AutoCloseKoinTest
import org.koin.test.junit5.KoinTestExtension
import java.io.File
import java.util.*
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class JsonOutputWrapperLoaderTest : AutoCloseKoinTest() {

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(configModule, module {
            factory<Debounce<Unit>> { (scope: CoroutineScope) -> NoDelayDebounce() }
        })
    }

    @Test
    fun storeProperties() {
        val file = File("build/resources/test", "test_outputs_stored.json")
        println("Test file location: ${file.absolutePath}")

        val testOutputConfigs = mutableListOf<OutputConfig>()
        val testOutputConfigWrappers = MutableList(5) { index ->
            val propertyProvider = JsonPropertyProvider(get(), TestJsonPropertySource())
            val config = OutputConfig(
                propertyProvider,
                "test_output",
                UUID.randomUUID().toString()
            )
            config.name = "Output #$index"
            config.pixels = 10 + index * 10
            config.colorOrder = ColorOrder.values().random()
            testOutputConfigs.add(config)
            OutputConfigWrapper(config.id, propertyProvider.getRawProperties(), config.outputIdentifier)
        }

        val configLoader =
            JsonOutputWrapperLoader(file, get())
        configLoader.storeOutputWrappers(testOutputConfigWrappers)
        assertTrue(file.isFile)

        // load output configs from file to validate stored data
        val loadedWrappers = configLoader.loadOutputWrappers()
        assertNotNull(loadedWrappers)
        assertEquals(testOutputConfigWrappers.size, loadedWrappers.size)
        assertContentEquals(testOutputConfigWrappers, loadedWrappers)

        val newTestOutputConfigs = loadedWrappers.map { wrapper ->
            val propertyProvider = JsonPropertyProvider(get(), TestJsonPropertySource(), wrapper.properties)
            OutputConfig(
                propertyProvider,
                wrapper.outputIdentifier,
                wrapper.id
            )
        }
        assertEquals(testOutputConfigs.size, newTestOutputConfigs.size)
        testOutputConfigs.forEachIndexed { index, config ->
            val newConfig = newTestOutputConfigs[index]
            assertEquals(config.id, newConfig.id)
            assertEquals(config.outputIdentifier, newConfig.outputIdentifier)
            assertEquals(config.name, newConfig.name)
            assertEquals(config.pixels, newConfig.pixels)
            assertEquals(config.colorOrder, newConfig.colorOrder)
        }
    }

    internal class TestJsonPropertySource : JsonPropertySource {
        private val properties = mutableMapOf<String, JsonNode>()

        override fun loadPropertyValues(): Map<String, JsonNode> {
            return properties
        }

        override fun storePropertyValues(properties: Map<String, JsonNode>) {
            this.properties.clear()
            this.properties.putAll(properties)
        }

        override fun getSource() = "Test Property Source"
    }

}