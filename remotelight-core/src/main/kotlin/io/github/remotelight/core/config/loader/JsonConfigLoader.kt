package io.github.remotelight.core.config.loader

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.remotelight.core.config.PropertyValuesWrapper
import io.github.remotelight.core.io.JsonFileLoader
import org.tinylog.kotlin.Logger
import java.io.File
import java.io.FileNotFoundException

class JsonConfigLoader(
    file: File,
    objectMapper: ObjectMapper = jacksonObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
) : ConfigLoader, JsonFileLoader(file, objectMapper) {

    override fun loadPropertyValues(): PropertyValuesWrapper? {
        try {
            return parseJson()
        } catch (e: FileNotFoundException) {
            Logger.info("Config file '${file.path}' does not exist.")
        } catch (e: Exception) {
            Logger.error(e, "An error occurred while loading the config from file '${file.absolutePath}'.")
        }
        return null
    }

    override fun storePropertyValues(valuesWrapper: PropertyValuesWrapper) {
        try {
            writeJson(valuesWrapper)
        } catch (e: Exception) {
            Logger.error(e, "An error occurred while storing the config to file '${file.absolutePath}'.")
        }
    }

}
