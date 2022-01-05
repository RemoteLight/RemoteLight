package io.github.remotelight.core.output.scene

import io.github.remotelight.core.color.Color
import io.github.remotelight.core.output.Output
import io.github.remotelight.core.utils.formatOutputInfo
import org.tinylog.kotlin.Logger

data class Scene(
    val id: String,
    val name: String,
    val entries: List<SceneEntry>
) {

    fun getEntryByOutputId(outputId: String) = entries.find { it.outputId == outputId }

    fun mapPixels(outputs: List<Output>, pixels: Array<Color>): Map<String, Array<Color?>> {
        return buildMap {
            outputs.forEach {
                try {
                    val mappedPixels = mapPixelsForOutput(it, pixels)
                    put(it.config.id, mappedPixels)
                } catch (e: Exception) {
                    Logger.error(e, "Something went wrong while mapping pixels for output ${it.formatOutputInfo()}.")
                }
            }
        }
    }

    private fun mapPixelsForOutput(output: Output, pixels: Array<Color>): Array<Color?> {
        val sceneEntry = getEntryByOutputId(output.config.id)
            ?: throw IllegalArgumentException("There is no scene entry for output ID ${output.config.id}.")

        val mappedPixels = Array<Color?>(output.config.pixels) { null }
        for (i in pixels.indices) {
            if (i >= sceneEntry.pixelMapping.size) break
            val mappedIndex = sceneEntry.pixelMapping[i]
            mappedPixels[mappedIndex] = pixels[i].copy()
        }
        return mappedPixels
    }

    fun verifyEntries(availableOutputs: List<Output>): List<SceneEntry> {
        val invalidSceneEntries = mutableListOf<SceneEntry>()
        entries.forEach { entry ->
            if (!availableOutputs.any { it.config.id == entry.outputId }) {
                invalidSceneEntries.add(entry)
            }
        }
        return invalidSceneEntries
    }

}