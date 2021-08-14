package io.github.remotelight.core.config.loader

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
import io.github.remotelight.core.RemoteLightCore
import io.github.remotelight.core.config.Property
import org.tinylog.kotlin.Logger
import java.io.File
import java.io.FileNotFoundException

class JsonConfigLoader(private val file: File): ConfigLoader {

    override fun loadProperties(): List<Property<*>> {
        try {
            file.reader().use { reader ->
                val jsonObj = Parser.default().parse(reader) as JsonObject?
                jsonObj?.array<Property<*>>("properties")?.let { array ->
                    Klaxon().parseArray<Property<*>>(array.toJsonString())?.let { return it }
                }
            }
        } catch (e: FileNotFoundException) {
            Logger.warn("Config file '${file.absolutePath}' does not exist.")
        } catch (e: Exception) {
            Logger.error(e, "An error occurred while loading the config from file '${file.absolutePath}'.")
        }
        return mutableListOf()
    }

    override fun storeProperties(properties: List<Property<*>>) {
        val obj = JsonObject()
        obj["version"] = RemoteLightCore.VERSION
        obj["properties"] = properties
        val objString = obj.toJsonString()
        val jsonString = (Parser.default().parse(StringBuilder(objString)) as JsonObject).toJsonString(true)
        try {
            file.writeText(jsonString)
        } catch (e: Exception) {
            Logger.error(e, "An error occurred while storing the config to file '${file.absolutePath}'.")
        }
    }

    override fun getSource() = "File: ${file.absolutePath}"

}