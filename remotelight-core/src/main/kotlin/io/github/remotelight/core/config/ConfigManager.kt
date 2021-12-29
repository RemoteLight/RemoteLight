package io.github.remotelight.core.config

import io.github.remotelight.core.config.loader.ConfigLoader
import io.github.remotelight.core.utils.Debounce
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.tinylog.kotlin.Logger

class ConfigManager(
    private val configLoader: ConfigLoader
) : ConfigChangeCallback, KoinComponent {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val debounce by inject<Debounce<Unit>> { parametersOf(scope) }

    override fun onConfigChange(properties: Map<String, Any?>) {
        if (!scope.isActive) {
            Logger.warn("Cannot store property values. Coroutine scope is closed!")
        }

        debounce.debounce {
            Logger.debug("Storing ${properties.size} property values to ${configLoader.getSource()}...")
            val wrapper = PropertyValuesWrapper(properties = properties)
            configLoader.storePropertyValues(wrapper)
            Logger.debug("Successfully stored property values.")
        }
    }

    /**
     * Load property values from the config loader and set them to the target config.
     */
    fun loadConfigPropertyValues(targetConfig: Config) {
        Logger.info("Loading property values from ${configLoader.getSource()}...")
        val wrapper = configLoader.loadPropertyValues()
        val properties = wrapper?.properties
        if (properties != null) {
            Logger.info("Successfully loaded ${properties.size} property values.")
            targetConfig.setPropertyValues(properties)
        } else {
            Logger.info("No property values available.")
        }
    }

    fun cancelScope() = scope.cancel()

}