package io.github.remotelight.core.utils

import io.github.remotelight.core.constants.FilePaths
import io.github.remotelight.core.constants.FilePaths.separate
import io.github.remotelight.core.constants.FilePaths.toFile
import org.tinylog.configuration.Configuration
import org.tinylog.kotlin.Logger
import java.util.*

object TinylogConfiguration {

    fun applyConfiguration() {
        if(Configuration.isFrozen()) {
            Logger.warn("Cannot apply tinylog configurations since it is already initialized.")
            return
        }
        val properties = loadTinylogProperties()
        val (fileFormat: String, latestFormat: String) = if(!checkPropertiesContainsKeys(properties)) {
            // fallback format
            Pair("{date}.{count}.txt", "latest.txt")
        } else{
            Pair(properties.getProperty("writerF.file"), properties.getProperty("writerF.latest"))
        }
        val logFilePath = FilePaths.LOG_FILE_PATH.toFile().absolutePath
        Configuration.set("writerF.file", logFilePath.separate(fileFormat))
        Configuration.set("writerF.latest", logFilePath.separate(latestFormat))
    }

    private fun loadTinylogProperties(): Properties {
        val properties = Properties()
        javaClass.classLoader.getResourceAsStream("tinylog.properties")?.use { stream ->
            properties.load(stream)
        }
        return properties
    }

    private fun checkPropertiesContainsKeys(properties: Properties) = !properties.isEmpty
            && properties.containsKey("writerF.file")
            && properties.containsKey("writerF.latest")

}