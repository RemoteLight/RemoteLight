package io.github.remotelight.core.output.config

import com.fasterxml.jackson.databind.JsonNode

interface OutputConfigLoadStoreSource {
    fun getPropertyValuesForOutput(outputId: String): Map<String, JsonNode>
    fun storePropertyValuesForOutput(outputId: String, properties: Map<String, JsonNode>)
}