package io.github.remotelight.core.config.loader

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import io.github.remotelight.core.RemoteLightCore
import io.github.remotelight.core.config.Property
import io.github.remotelight.core.io.JsonFileLoader
import org.tinylog.kotlin.Logger
import java.io.File

class JsonConfigLoader(file: File): JsonFileLoader(file), ConfigLoader {

    override fun loadProperties(): List<Property<*>> {
        try {
            val jsonObj = parseJson() as? JsonObject?
            jsonObj?.array<Property<*>>("properties")?.let { array ->
                Klaxon().parseArray<Property<*>>(array.toJsonString())?.let { return it }
            }
        } catch (e: Exception) {
            Logger.error(e, "An error occurred while loading the config from file '${file.absolutePath}'.")
        }
        return mutableListOf()
    }

    override fun storeProperties(properties: List<Property<*>>) {
        val obj = JsonObject()
        obj["version"] = RemoteLightCore.VERSION
        obj["properties"] = properties
        try {
            writeJson(obj, true)
        } catch (e: Exception) {
            Logger.error(e, "An error occurred while storing the config to file '${file.absolutePath}'.")
        }
    }

}