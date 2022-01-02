package io.github.remotelight.core.output

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.remotelight.core.color.Color
import io.github.remotelight.core.config.BaseConfigTest
import io.github.remotelight.core.config.provider.JsonPropertyProvider
import io.github.remotelight.core.di.configModule
import io.github.remotelight.core.output.config.*
import io.github.remotelight.core.tools.NoDelayDebounce
import io.github.remotelight.core.utils.Debounce
import kotlinx.coroutines.CoroutineScope
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.test.junit5.KoinTestExtension
import java.util.*
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class OutputManagerTest {

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(configModule, module {
            factory<Debounce<Unit>> { (scope: CoroutineScope) -> NoDelayDebounce() }
        })
    }

    @Test
    fun loadStore() {
        val configManager = TestOutputConfigManager(5)
        val manager = OutputManager(configManager, TestOutputRegistry)
        assertEquals(5, manager.getOutputs().size)
        assertContentEquals(configManager.outputConfigs, manager.getOutputs().map { it.config })

        // auto save test
        configManager.outputConfigs.clear()
        assertEquals(0, configManager.outputConfigs.size)
        manager.getOutputs()[0].config.pixels = 1
        assertEquals(5, configManager.outputConfigs.size)

        // store test
        configManager.outputConfigs.clear()
        assertEquals(0, configManager.outputConfigs.size)
        manager.storeOutputs()
        assertEquals(5, configManager.outputConfigs.size)
    }

    @Test
    fun createRemove() {
        val configManager = TestOutputConfigManager(1)
        val manager = OutputManager(configManager, TestOutputRegistry)

        val output = manager.createAndAddOutput("test_output")
        assertEquals(TestOutput::class, output::class)
        assertEquals(2, manager.getOutputs().size)
        assertEquals(2, configManager.outputConfigs.size)

        assertThrows<IllegalArgumentException> {
            val invalidOutput = manager.createAndAddOutput("invalid_output")
            assertNull(invalidOutput)
        }

        // remove output
        manager.removeOutput(output.config.id)
        assertEquals(1, manager.getOutputs().size)
        assertEquals(1, configManager.outputConfigs.size)

        assertThrows<IllegalArgumentException> {
            manager.removeOutput("invalid id")
        }
    }

    internal class TestOutputConfigManager(amount: Int = 2) : OutputConfigManager, OutputConfigLoadStoreSource {
        private var outputConfigSource: OutputConfigSource? = null

        val outputConfigs = MutableList(amount) {
            val id = UUID.randomUUID().toString()
            val propertySource = JsonOutputPropertySource(id, this)
            OutputConfig(
                JsonPropertyProvider(jacksonObjectMapper(), propertySource),
                "test_output",
                id)
        }

        override fun attachOutputConfigSource(outputConfigSource: OutputConfigSource) {
            this.outputConfigSource = outputConfigSource
        }

        override fun loadOutputConfigs(): List<OutputConfig> = outputConfigs

        override fun storeOutputConfigs(outputConfigs: List<OutputConfig>) {
            this.outputConfigs.clear()
            this.outputConfigs.addAll(outputConfigs)
        }

        override fun createOutputConfig(outputIdentifier: OutputIdentifier, id: String): OutputConfig {
            return OutputConfig(
                BaseConfigTest.TestPropertyProvider(0),
                outputIdentifier,
                id
            )
        }

        @Suppress("UNCHECKED_CAST")
        override fun getPropertyValuesForOutput(outputId: String): Map<String, JsonNode> {
            return outputConfigSource?.invoke()
                ?.find { it.id == outputId }
                ?.getProperties().orEmpty() as Map<String, JsonNode>
        }

        override fun storePropertyValuesForOutput(outputId: String, properties: Map<String, JsonNode>) {
            outputConfigSource?.invoke()?.let { storeOutputConfigs(it) }
        }
    }

    internal object TestOutputRegistry : OutputRegistry {
        override fun createOutput(outputIdentifier: OutputIdentifier, outputConfig: OutputConfig): Output? {
            return if (outputIdentifier == "test_output") {
                TestOutput(outputConfig)
            } else {
                DefaultOutputRegistry.createOutput(outputIdentifier, outputConfig)
            }
        }

        override fun registerOutput(outputIdentifier: OutputIdentifier, factory: OutputFactory<*>) {}
    }

    internal class TestOutput(config: OutputConfig) : Output(config) {
        var lastOutputPixels: Array<Color>? = null

        override fun onActivate(): OutputStatus {
            return OutputStatus.Connected
        }

        override fun onDeactivate(): OutputStatus {
            return OutputStatus.Disconnected
        }

        override fun onOutputPixels(pixels: Array<Color>) {
            lastOutputPixels = pixels
        }
    }

}