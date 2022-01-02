package io.github.remotelight.core.output.config

import com.fasterxml.jackson.databind.JsonNode
import io.github.remotelight.core.config.loader.JsonPropertySource

class JsonOutputPropertySource(
    private val outputId: String,
    private val loadStoreSource: OutputConfigLoadStoreSource
) : JsonPropertySource {

    override fun loadPropertyValues(): Map<String, JsonNode> {
        return loadStoreSource.getPropertyValuesForOutput(outputId)
    }

    override fun storePropertyValues(properties: Map<String, JsonNode>) {
        loadStoreSource.storePropertyValuesForOutput(outputId, properties)
    }

    override fun getSource() = "JSON Output Config ($outputId)"

}
