package io.github.remotelight.core

import io.github.remotelight.core.di.Modules
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.tinylog.kotlin.Logger
import kotlin.coroutines.CoroutineContext


class RemoteLightCore: KoinComponent {

    companion object {
        const val VERSION = "1.0.0"

        var isInitialized = false
            private set
    }

    init {
        isInitialized = true
        initKoin()
        Logger.info("Initialized RemoteLightCore version $VERSION")
    }

    private fun initKoin() {
        startKoin {
            modules(Modules.coreModule)
        }
    }

    fun destroy() {
        val coroutineContext: CoroutineContext = get()
        runBlocking {
            coroutineContext[Job]?.apply {
                if(!isCompleted) {
                    // if the background operation is not finished after 10 sec, it will be terminated
                    val timeExceeded = withTimeoutOrNull(10_000L) {
                        Logger.info("Waiting for background job to complete...")
                        cancelAndJoin()
                        true
                    }
                    if(timeExceeded != true) {
                        Logger.warn("Timeout exceeded, background job was terminated! Data may not have been saved in time.")
                    }
                }
            }
        }
    }

}