package io.github.remotelight.core.config.loader

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.remotelight.core.config.PropertyValuesWrapper
import io.github.remotelight.core.error.EmptyConfigException
import io.github.remotelight.core.io.JsonFileLoader
import java.io.File
import java.io.FileNotFoundException

class JsonConfigLoader(
    file: File,
    objectMapper: ObjectMapper
) : JsonFileLoader(file, objectMapper), JsonPropertySource {

    override fun loadPropertyValues(): Map<String, JsonNode> {
        try {
            val wrapper: PropertyValuesWrapper<JsonNode> = parseJson()
            return wrapper.properties
        } catch (e: FileNotFoundException) {
            throw EmptyConfigException("Config file '${file.absolutePath}' does not exist.", e)
        }

    }

    override fun storePropertyValues(properties: Map<String, JsonNode>) {
        val wrapper = PropertyValuesWrapper(properties = properties)
        writeJson(wrapper)
    }

}
