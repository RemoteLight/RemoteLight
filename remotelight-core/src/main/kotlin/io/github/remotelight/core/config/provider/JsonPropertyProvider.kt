package io.github.remotelight.core.config.provider

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import io.github.remotelight.core.config.loader.JsonPropertySource
import io.github.remotelight.core.error.EmptyConfigException
import io.github.remotelight.core.utils.Debounce
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.tinylog.kotlin.Logger

class JsonPropertyProvider(
    private val objectMapper: ObjectMapper,
    private val propertySource: JsonPropertySource,
    initialProperties: Map<String, JsonNode>? = null
) : PropertyProvider<JsonNode>, KoinComponent {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val debounce by inject<Debounce<Unit>> { parametersOf(scope) }

    private val properties = mutableMapOf<String, JsonNode>().apply {
        if (initialProperties != null) putAll(initialProperties)
    }

    override fun onInit() {
        try {
            properties.putAll(propertySource.loadPropertyValues())
        } catch (e: EmptyConfigException) {
            Logger.info("Config is not available: ${e.message}")
        } catch (e: Exception) {
            Logger.error(e, "Failed to load property values from ${propertySource.getSource()}")
        }
    }

    override fun onClose() {
        scope.cancel()
    }

    override fun storeProperties() {
        debounce.debounce {
            propertySource.storePropertyValues(properties)
        }
    }

    override fun getProperties(): Map<String, *> {
        return objectMapper.convertValue<Map<String, Any?>>(properties)
    }

    override fun getRawProperties() = properties.toMap()

    override fun <T> getProperty(id: String, type: Class<T>): T {
        return parseJsonNode(properties[id], type)
    }

    override fun hasProperty(id: String): Boolean {
        return properties.containsKey(id)
    }

    override fun <T> setProperty(id: String, value: T) {
        properties[id] = objectMapper.valueToTree(value)
    }

    override fun deleteProperty(id: String): Any? {
        val oldValue = properties.remove(id) ?: return null
        return parseJsonNode(oldValue, Any::class.java)
    }

    override fun clear() {
        properties.clear()
    }

    private fun <T> parseJsonNode(jsonNode: JsonNode?, type: Class<T>): T {
        return objectMapper.treeToValue(jsonNode, type)
    }

}