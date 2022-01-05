package io.github.remotelight.core.output.scene.loader

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.remotelight.core.io.JsonFileLoader
import io.github.remotelight.core.output.scene.Scene
import org.tinylog.kotlin.Logger
import java.io.File
import java.io.FileNotFoundException

class JsonSceneLoader(
    file: File,
    objectMapper: ObjectMapper
) : SceneLoader, JsonFileLoader(file, objectMapper) {

    override fun loadScenes(): List<Scene>? {
        try {
            return parseJson()
        } catch (e: FileNotFoundException) {
            Logger.info("Scenes data file '${file.path}' does not exist.")
        } catch (e: Exception) {
            Logger.error(e, "An error occurred while loading scenes from file '${file.absolutePath}'.")
        }
        return null
    }

    override fun storeScenes(scenes: List<Scene>) {
        try {
            writeJson(scenes)
        } catch (e: Exception) {
            Logger.error(e, "An error occurred while storing scenes to file '${file.absolutePath}'.")
        }
    }

}