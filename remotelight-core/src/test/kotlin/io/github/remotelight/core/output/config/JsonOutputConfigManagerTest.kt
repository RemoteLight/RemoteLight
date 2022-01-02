package io.github.remotelight.core.output.config

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import io.github.remotelight.core.di.configModule
import io.github.remotelight.core.output.OutputIdentifier
import io.github.remotelight.core.output.config.loader.OutputWrapperLoader
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
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class JsonOutputConfigManagerTest : AutoCloseKoinTest() {

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
        val manager = JsonOutputConfigManager(get(), TestOutputWrapperLoader(5))
        val configs = manager.loadOutputConfigs()

        assertNotNull(configs)
        assertEquals(5, configs.size)
        configs.forEachIndexed { index, config ->
            assertEquals(testOutputIdentifier, config.outputIdentifier)
            assertEquals(generatePixels(index), config.pixels)
        }
    }

    @Test
    fun storeTest() {
        val mapper = get<ObjectMapper>()
        val loader = TestOutputWrapperLoader(5)
        val manager = JsonOutputConfigManager(get(), loader)
        val configs = manager.loadOutputConfigs()
        loader.outputConfigWrappers.clear()

        assertNotNull(configs)
        assertEquals(5, configs.size)
        manager.attachOutputConfigSource { configs }

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
            val pixels = loader.outputConfigWrappers[0].properties["pixels"]
            assertNotNull(pixels)
            assertEquals(2, mapper.treeToValue(pixels))
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
        internal const val testOutputIdentifier: OutputIdentifier = "test_output"
    }

    internal class TestOutputWrapperLoader(amount: Int = 5) : OutputWrapperLoader<JsonNode> {

        private val mapper = jacksonObjectMapper()

        internal val outputConfigWrappers = MutableList(amount) { index ->
            val properties = buildMap<String, JsonNode> {
                put("name", mapper.valueToTree("Test-Output-#$index"))
                put("pixels", mapper.valueToTree(generatePixels(index)))
            }
            OutputConfigWrapper(UUID.randomUUID().toString(), properties, testOutputIdentifier)
        }

        override fun loadOutputWrappers(): List<OutputConfigWrapper<JsonNode>> {
            return outputConfigWrappers
        }

        override fun storeOutputWrappers(outputWrappers: List<OutputConfigWrapper<JsonNode>>) {
            this.outputConfigWrappers.clear()
            this.outputConfigWrappers.addAll(outputWrappers)
        }

        override fun getSource() = "Test Output Config"
    }

}