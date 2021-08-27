package io.github.remotelight.core.io

import com.beust.klaxon.JsonBase
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import org.tinylog.kotlin.Logger
import java.io.File
import java.io.FileNotFoundException

open class JsonFileLoader(val file: File): Loader {

    fun parseJson(): Any? {
        try {
            file.reader().use { reader ->
                return Parser.default().parse(reader)
            }
        } catch (e: FileNotFoundException) {
            Logger.warn("The JSON file '${file.absolutePath}' does not exist.")
        }
        return null
    }

    fun writeJson(json: JsonBase, prettyPrint: Boolean = true) {
        val jsonString = if(prettyPrint) {
            // this is the recommended way to pretty print json with Klaxon (https://github.com/cbeust/klaxon/issues/98#issuecomment-361169101)
            // convert to JSON String, parse it and convert it to json string with pretty print
            val objString = json.toJsonString()
            (Parser.default().parse(StringBuilder(objString)) as JsonObject).toJsonString(true)
        } else {
            json.toJsonString(false)
        }
        file.writeText(jsonString)
    }

    override fun getSource() = "File: ${file.absolutePath}"

}