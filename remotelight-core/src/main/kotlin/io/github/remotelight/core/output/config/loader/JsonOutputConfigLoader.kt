package io.github.remotelight.core.output.config.loader

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.remotelight.core.io.JsonFileLoader
import io.github.remotelight.core.output.config.OutputConfigWrapper
import org.tinylog.kotlin.Logger
import java.io.File
import java.io.FileNotFoundException

class JsonOutputConfigLoader(
    file: File,
    objectMapper: ObjectMapper
) : OutputConfigLoader, JsonFileLoader(file, objectMapper) {

    override fun loadOutputConfigs(): List<OutputConfigWrapper>? {
        try {
            return parseJson()
        } catch (e: FileNotFoundException) {
            Logger.info("Output config file '${file.path}' does not exist.")
        } catch (e: Exception) {
            Logger.error(e, "An error occurred while loading the output configs from file '${file.absolutePath}'.")
        }
        return null
    }

    override fun storeOutputConfigs(outputConfigs: List<OutputConfigWrapper>) {
        try {
            writeJson(outputConfigs)
        } catch (e: Exception) {
            Logger.error(e, "An error occurred while storing the config to file '${file.absolutePath}'.")
        }
    }
}