package io.github.remotelight.core.io

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

open class JsonFileLoader(
    val file: File,
    val objectMapper: ObjectMapper = jacksonObjectMapper()
) : Loader {

    inline fun <reified T> parseJson(): T {
        file.inputStream().use { stream ->
            return objectMapper.readValue(stream)
        }
    }

    inline fun <reified T> writeJson(data: T) {
        file.outputStream().use { stream ->
            objectMapper.writeValue(stream, data)
        }
    }

    override fun getSource() = "File: ${file.absolutePath}"

}