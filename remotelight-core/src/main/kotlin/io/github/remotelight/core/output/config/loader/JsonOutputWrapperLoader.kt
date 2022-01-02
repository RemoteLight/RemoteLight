package io.github.remotelight.core.output.config.loader

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.remotelight.core.io.JsonFileLoader
import io.github.remotelight.core.output.config.OutputConfigWrapper
import org.tinylog.kotlin.Logger
import java.io.File
import java.io.FileNotFoundException

class JsonOutputWrapperLoader(
    file: File,
    objectMapper: ObjectMapper
) : OutputWrapperLoader<JsonNode>, JsonFileLoader(file, objectMapper) {

    override fun loadOutputWrappers(): List<OutputConfigWrapper<JsonNode>>? {
        try {
            return parseJson()
        } catch (e: FileNotFoundException) {
            Logger.info("Output config file '${file.path}' does not exist.")
        } catch (e: Exception) {
            Logger.error(e, "An error occurred while loading the output configs from file '${file.absolutePath}'.")
        }
        return null
    }

    override fun storeOutputWrappers(outputWrappers: List<OutputConfigWrapper<JsonNode>>) {
        try {
            writeJson(outputWrappers)
        } catch (e: Exception) {
            Logger.error(e, "An error occurred while storing the config to file '${file.absolutePath}'.")
        }
    }

}