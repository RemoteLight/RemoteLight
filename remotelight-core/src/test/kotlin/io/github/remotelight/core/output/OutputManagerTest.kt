package io.github.remotelight.core.output

import io.github.remotelight.core.color.Color
import io.github.remotelight.core.output.config.OutputConfig
import io.github.remotelight.core.output.config.OutputConfigManager
import io.github.remotelight.core.output.config.OutputConfigSource
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class OutputManagerTest {

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

    internal class TestOutputConfigManager(amount: Int = 2) : OutputConfigManager {
        private var outputConfigSource: OutputConfigSource? = null

        val outputConfigs = MutableList(amount) {
            OutputConfig(this, "test_output", UUID.randomUUID().toString())
        }

        override fun enableAutoSave(outputConfigSource: OutputConfigSource) {
            this.outputConfigSource = outputConfigSource
        }

        override fun loadOutputConfigs(): List<OutputConfig> = outputConfigs

        override fun storeOutputConfigs(outputConfigs: List<OutputConfig>) {
            this.outputConfigs.clear()
            this.outputConfigs.addAll(outputConfigs)
        }

        override fun onConfigChange(properties: Map<String, Any?>) {
            outputConfigSource?.let { source ->
                storeOutputConfigs(source())
            }
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