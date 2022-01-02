package io.github.remotelight.core.config.loader

import com.fasterxml.jackson.databind.JsonNode

interface JsonPropertySource {

    fun loadPropertyValues(): Map<String, JsonNode>

    fun storePropertyValues(properties: Map<String, JsonNode>)

    fun getSource(): String

}