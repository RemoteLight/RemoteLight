package io.github.remotelight.core

import io.github.remotelight.core.config.ConfigManager
import io.github.remotelight.core.config.GlobalConfig
import io.github.remotelight.core.di.coreModule
import io.github.remotelight.core.utils.TinylogConfiguration
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.tinylog.kotlin.Logger
import org.tinylog.provider.ProviderRegistry
import kotlin.concurrent.thread

class RemoteLightCore : KoinComponent {

    companion object {
        const val VERSION = "1.0.0"

        var isInitialized = false
            private set
    }

    private val shutdownHook: Thread = thread(start = false, name = "Shutdown Thread") { destroy() }

    init {
        if (isInitialized)
            throw IllegalStateException("RemoteLightCore is already initialized.")
        isInitialized = true
        TinylogConfiguration.applyConfiguration()
        initKoin()
        initConfig()
        Runtime.getRuntime().addShutdownHook(shutdownHook)
        Logger.info("Initialized RemoteLightCore version $VERSION")
    }

    private fun initKoin() {
        startKoin {
            modules(coreModule)
        }
    }

    private fun initConfig() {
        val globalConfigManager: ConfigManager = get(named("global"))
        globalConfigManager.loadConfigPropertyValues(GlobalConfig)
    }

    /**
     * Destroy this [RemoteLightCore] instance.
     * It will close used dependencies and cancel all jobs that were started
     * using the shared coroutine context.
     */
    fun destroy() {
        runBlocking {
            stopKoin()
            ProviderRegistry.getLoggingProvider().shutdown() // shutdown tinylog
            isInitialized = false
        }

        // destroy function was not run from shutdown hook, remove the hook
        if (Thread.currentThread().id != shutdownHook.id) {
            Runtime.getRuntime().removeShutdownHook(shutdownHook)
        }
    }

}