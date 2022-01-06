package io.github.remotelight.core.output.scene

import io.github.remotelight.core.color.Color
import io.github.remotelight.core.constants.Defaults
import io.github.remotelight.core.output.Output
import io.github.remotelight.core.utils.formatInfo
import org.tinylog.kotlin.Logger

data class Scene(
    val id: String,
    var name: String,
    var loopInterval: Long = Defaults.SCENE_LOOP_INTERVAL,
    var entries: List<SceneEntry>,
    var pixelCount: Int
) {

    fun getEntryByOutputId(outputId: String) = entries.find { it.outputId == outputId }

    fun outputPixels(outputs: List<Output>, pixels: Array<Color>) {
        if (pixels.size != pixelCount) {
            throw IllegalArgumentException("Required pixel array of length $pixelCount, but got ${pixels.size}.")
        }
        outputs.forEach { output ->
            try {
                val mappedPixels = mapPixelsForOutput(output, pixels)
                output.outputPixels(mappedPixels)
            } catch (e: Exception) {
                Logger.error(e, "Something went wrong while mapping pixels for output ${output.formatInfo()}.")
            }
        }
    }

    private fun mapPixelsForOutput(output: Output, pixels: Array<Color>): Array<Color> {
        val sceneEntry = getEntryByOutputId(output.config.id)
            ?: throw IllegalArgumentException("There is no scene entry for output ID ${output.config.id}.")

        val mappedPixels = Array(output.config.pixels) { Color.BLACK }
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