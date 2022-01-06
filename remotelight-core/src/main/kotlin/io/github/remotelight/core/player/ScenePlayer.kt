package io.github.remotelight.core.player

import io.github.remotelight.core.color.Color
import io.github.remotelight.core.color.ColorBase
import io.github.remotelight.core.output.Output
import io.github.remotelight.core.output.scene.Scene
import io.github.remotelight.core.utils.CoroutineTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.tinylog.kotlin.Logger
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.time.Duration

class ScenePlayer(
    private val scene: Scene,
    private val outputs: List<Output>,
    loopInterval: Duration
) {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    var loopInterval = loopInterval
        set(value) {
            field = value
            timer.interval = value
        }

    private val playbackContents = CopyOnWriteArrayList<PlaybackContent>()

    private val timer = CoroutineTimer(loopInterval)

    val framesPerSecond get() = timer.framesPerSecond

    fun addContent(content: PlaybackContent) {
        val index = findIndexForPriority(content.playbackPriority)
        playbackContents.add(index, content)
    }

    fun removeContent(content: PlaybackContent) {
        playbackContents.remove(content)
    }

    fun getPlaybackContents() = playbackContents.toList()

    private fun findIndexForPriority(priority: Int): Int {
        var index = 0
        for (i in playbackContents.indices) {
            val content = playbackContents[i]
            if (content.playbackPriority <= priority) {
                index = i
            } else {
                break
            }
        }
        return index
    }

    suspend fun start() {
        stop()
        playbackContents.forEach { it.onPlayerEnable(scope) }
        timer.start(scope) {
            val pixelBuffer = Array(scene.pixelCount) { Color.BLACK }

            playbackContents.filter { it.hasContent() }.forEach { content ->
                val producedPixels = content.produce()
                if (producedPixels != null) {
                    try {
                        copyPixelArray(producedPixels, pixelBuffer)
                    } catch (e: Exception) {
                        Logger.error(
                            e, "Failed to copy pixels produced by ${content.getDescription()} into output pixel buffer."
                        )
                    }
                }
            }

            //Logger.debug("FPS: ${timer.framesPerSecond.replayCache.lastOrNull()}")

            scene.outputPixels(outputs, pixelBuffer)
        }
    }

    suspend fun stop() {
        if (isRunning()) {
            timer.stopAndJoin()
            scope.cancel()
            playbackContents.forEach { it.onPlayerDisable() }
        }
    }

    fun isRunning() = timer.isRunning()

    private fun copyPixelArray(source: Array<out ColorBase>, destination: Array<Color>) {
        for (i in source.indices) {
            val pixel = source[i]
            if (pixel is Color) { // ignore empty colors
                destination[i] = pixel
            }
        }
    }

}