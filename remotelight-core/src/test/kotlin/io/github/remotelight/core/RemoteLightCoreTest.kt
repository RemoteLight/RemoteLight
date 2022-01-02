package io.github.remotelight.core

import io.github.remotelight.core.config.GlobalConfig
import io.github.remotelight.core.config.provider.PropertyProvider
import io.github.remotelight.core.constants.FilePaths
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.io.File
import kotlin.test.assertEquals

internal class RemoteLightCoreTest {

    companion object {
        private lateinit var core: RemoteLightCore

        @BeforeAll
        @JvmStatic
        fun initializeCore() {
            println("Setting file paths for testing...")
            FilePaths.apply {
                ROOT_LOCATION = "build${File.separator}resources${File.separator}test"
            }
            println("Initialize RemoteLightCore...")
            core = RemoteLightCore()
        }

        @AfterAll
        @JvmStatic
        fun destroyRemoteLight() {
            println("Destroying RemoteLightCore")
            core.destroy()
        }
    }

    @Test
    fun testDIModules() {
        val testComponent = object : KoinComponent {
            val configLoaderGlobal: PropertyProvider<*> by inject(qualifier = named("global"))
        }
        testComponent.apply {
            assertNotNull(configLoaderGlobal)
        }
    }

    @Test
    fun testGlobalConfig() {
        assertNotNull(GlobalConfig.test)
        assertEquals("Hello World!", GlobalConfig.test)
    }

}