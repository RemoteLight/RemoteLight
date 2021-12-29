package io.github.remotelight.core.output.config

import io.github.remotelight.core.di.configModule
import io.github.remotelight.core.output.OutputDescriptor
import io.github.remotelight.core.output.OutputIdentifier
import io.github.remotelight.core.output.config.loader.OutputConfigLoader
import io.github.remotelight.core.utils.CoolDownDebounce
import io.github.remotelight.core.utils.Debounce
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.test.junit5.AutoCloseKoinTest
import org.koin.test.junit5.KoinTestExtension
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class OutputConfigManagerTest : AutoCloseKoinTest() {

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(configModule, module {
            // override debounce module to reduce delay
            factory<Debounce<Unit>> { (scope: CoroutineScope) -> CoolDownDebounce(50L, scope) }
        })
    }

    @Test
    fun loadTest() {
        val manager = OutputConfigManager(TestOutputConfigLoader(5))
        val configs = manager.loadOutputConfigs()

        assertNotNull(configs)
        assertEquals(5, configs.size)
        configs.forEachIndexed { index, config ->
            assertEquals(TestOutputDescriptor(), config.outputDescriptor)
            assertEquals(generatePixels(index), config.pixels)
        }
    }

    @Test
    fun storeTest() {
        val loader = TestOutputConfigLoader(5)
        val manager = OutputConfigManager(loader)
        val configs = manager.loadOutputConfigs()
        loader.outputConfigWrappers.clear()

        assertNotNull(configs)
        assertEquals(5, configs.size)
        manager.enableAutoSave { configs }

        runBlocking {
            manager.storeOutputConfigs(configs)
            delay(10)
            assertEquals(5, loader.outputConfigWrappers.size)

            // test debounce
            loader.outputConfigWrappers.clear()
            manager.storeOutputConfigs(configs)
            delay(10)
            assertEquals(0, loader.outputConfigWrappers.size)
            delay(get<Debounce<Unit>> { parametersOf(this) }.delay - 10)
            assertEquals(5, loader.outputConfigWrappers.size)
            loader.outputConfigWrappers.clear()
            delay(get<Debounce<Unit>> { parametersOf(this) }.delay)

            // test auto save
            configs[0].pixels = 2
            delay(10)
            assertEquals(5, loader.outputConfigWrappers.size)
            assertEquals(2, loader.outputConfigWrappers[0].properties["pixels"])
            loader.outputConfigWrappers.clear()

            // test cancel scope
            manager.cancelScope()
            manager.storeOutputConfigs(configs)
            delay(10)
            assertEquals(0, loader.outputConfigWrappers.size)
        }
    }

    companion object {
        internal fun generatePixels(index: Int) = index * 10
    }

    internal class TestOutputConfigLoader(amount: Int = 5) : OutputConfigLoader {

        internal val outputConfigWrappers = MutableList(amount) { index ->
            val properties = buildMap<String, Any?> {
                put("name", "Test-Output-#$index")
                put("pixels", generatePixels(index))
            }
            OutputConfigWrapper(properties, TestOutputDescriptor())
        }

        override fun loadOutputConfigs(): List<OutputConfigWrapper> = outputConfigWrappers

        override fun storeOutputConfigs(outputConfigs: List<OutputConfigWrapper>) {
            this.outputConfigWrappers.clear()
            this.outputConfigWrappers.addAll(outputConfigs)
        }

        override fun getSource() = "Test Output Config"
    }

    internal data class TestOutputDescriptor(
        override val uniqueIdentifier: OutputIdentifier = "test_output",
        override val displayName: String = "Test Output"
    ) : OutputDescriptor

}