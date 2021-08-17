package io.github.remotelight.core

import io.github.remotelight.core.di.Modules
import io.github.remotelight.core.utils.TinylogConfiguration
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.tinylog.kotlin.Logger
import org.tinylog.provider.ProviderRegistry
import kotlin.concurrent.thread
import kotlin.coroutines.CoroutineContext

class RemoteLightCore: KoinComponent {

    companion object {
        const val VERSION = "1.0.0"

        var isInitialized = false
            private set
    }

    private val shutdownHook: Thread = thread(start = false, name = "Shutdown Thread") { destroy() }

    init {
        if(isInitialized)
            throw IllegalStateException("RemoteLightCore is already initialized.")
        isInitialized = true
        TinylogConfiguration.applyConfiguration()
        initKoin()
        Runtime.getRuntime().addShutdownHook(shutdownHook)
        Logger.info("Initialized RemoteLightCore version $VERSION")
    }

    private fun initKoin() {
        startKoin {
            modules(Modules.coreModule)
        }
    }

    /**
     * Destroy this [RemoteLightCore] instance.
     * It will release close used dependencies and cancel all jobs that were started
     * using the shared coroutine context.
     */
    fun destroy() {
        runBlocking {
            cancelCoroutineJobs()

            stopKoin()
            ProviderRegistry.getLoggingProvider().shutdown() // shutdown tinylog
            isInitialized = false
        }

        // destroy function was not run from shutdown hook, remove the hook
        if(Thread.currentThread().id != shutdownHook.id) {
            Runtime.getRuntime().removeShutdownHook(shutdownHook)
        }
    }

    /**
     * Cancels jobs that were started using the shared coroutine context.
     * It will wait until the child jobs were finished, but not longer than
     * the defined timeout.
     */
    private suspend fun cancelCoroutineJobs(timeout: Long = 10_000L) {
        val coroutineContext: CoroutineContext = get()
        coroutineContext[Job]?.apply {
            if (!isCompleted) {
                // if the background operation is not finished after the timeout, it will be terminated
                val timeExceeded = withTimeoutOrNull(timeout) {
                    Logger.info("Waiting for background job to complete...")
                    cancelAndJoin()
                    true
                }
                if (timeExceeded != true) {
                    Logger.warn("Timeout exceeded, background job was terminated! Data may not have been saved in time.")
                }
            }
        }
    }

}