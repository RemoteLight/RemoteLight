package io.github.remotelight.core.propeties.loader

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
import io.github.remotelight.core.RemoteLightCore
import io.github.remotelight.core.propeties.Property
import java.io.File

class JsonConfigLoader(private val file: File): ConfigLoader {

    override fun loadProperties(): List<Property> {
        val jsonObj = Parser.default().parse(file.reader()) as JsonObject?
        jsonObj?.array<Property>("properties")?.let { array ->
            Klaxon().parseArray<Property>(array.toJsonString())?.let { return it }
        }
        return mutableListOf()
    }

    override fun storeProperties(properties: List<Property>) {
        val obj = JsonObject()
        obj["version"] = RemoteLightCore.VERSION
        obj["properties"] = properties
        val jsonString = obj.toJsonString(prettyPrint = true)
        file.writeText(jsonString)
    }

}