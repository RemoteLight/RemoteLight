package io.github.remotelight.core.config

import io.github.remotelight.core.di.configModule
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
import org.koin.test.junit5.KoinTestExtension
import org.tinylog.kotlin.Logger
import kotlin.test.assertEquals

internal class ConfigManagerTest : BaseConfigTest() {

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        allowOverride(true)
        modules(configModule, module {
            // override debounce module to reduce delay
            factory<Debounce<Unit>> { (scope: CoroutineScope) -> CoolDownDebounce(50L, scope) }
        })
    }

    @Test
    fun loadTest() {
        val manager = ConfigManager(TestConfigLoader(2))
        val config = object : Config(manager) {}

        assertEquals(0, config.getPropertyValues().size)
        manager.loadConfigPropertyValues(config)
        assertEquals(2, config.getPropertyValues().size)
    }

    @Test
    fun storeTest() {
        val loader = TestConfigLoader(0)
        val manager = ConfigManager(loader)
        val config = object : Config(manager) {}

        runBlocking {
            manager.loadConfigPropertyValues(config)
            assertEquals(0, config.getPropertyValues().size)
            config.storeProperty("test", 1)
            delay(10)
            assertEquals(1, loader.properties.size)

            // test debounce
            config.storeProperty("test2", 2)
            delay(10)
            assertEquals(1, loader.properties.size)

            delay(get<Debounce<Unit>> { parametersOf(this) }.delay)
            Logger.debug("Asserting")
            assertEquals(2, loader.properties.size)

            // test cancel scope
            manager.cancelScope()
            config.storeProperty("test3", 3)
            delay(10)
            assertEquals(2, loader.properties.size)
        }
    }

}