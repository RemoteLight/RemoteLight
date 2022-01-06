package io.github.remotelight.core.player

import io.github.remotelight.core.color.Color
import io.github.remotelight.core.color.ColorBase
import io.github.remotelight.core.color.EmptyColor
import io.github.remotelight.core.config.BaseConfigTest
import io.github.remotelight.core.effect.StripPainter
import io.github.remotelight.core.output.Output
import io.github.remotelight.core.output.OutputStatus
import io.github.remotelight.core.output.config.OutputConfig
import io.github.remotelight.core.output.scene.Scene
import io.github.remotelight.core.output.scene.SceneEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.*
import kotlin.time.Duration.Companion.milliseconds

internal class ScenePlayerTest {

    @Test
    fun testPlayer() {
        val pixelCount = 5
        val outputs = List(2) { i ->
            DummyOutput("$i").apply {
                config.pixels = pixelCount
            }
        }
        val sceneEntries = outputs.map {
            SceneEntry(
                it.config.id,
                List(pixelCount) { index -> index }
            )
        }
        val scene = Scene(
            "test_scene",
            "Test Scene",
            sceneEntries,
            pixelCount
        )

        val interval = 30.milliseconds
        val player = ScenePlayer(scene, outputs, interval)
        assertFalse(player.isRunning())

        val content1 = TestPlaybackContent(10, 1)
        val content2 = TestPlaybackContent(0, 2)

        player.addContent(content1)
        assertEquals(1, player.getPlaybackContents().size)
        player.addContent(content2)
        assertEquals(2, player.getPlaybackContents().size)
        assertEquals(content2, player.getPlaybackContents()[0])
        assertEquals(content1, player.getPlaybackContents()[1])

        runBlocking {
            player.start()
            delay(10)
            assertTrue(player.isRunning())
            assertTrue(content1.isEnabled)
            assertTrue(content2.isEnabled)

            assertNotNull(outputs[0].pixels)
            assertNotNull(outputs[1].pixels)

            val stripPainter = StripPainter(pixelCount, Color.RED)
            content2.pixels = stripPainter.buffer.clone()
            delay(interval)
            assertContentEquals(stripPainter.buffer, outputs[0].pixels)
            assertContentEquals(stripPainter.buffer, outputs[1].pixels)

            // test priority pixel output
            stripPainter.all(Color.GREEN)
            content1.pixels = stripPainter.buffer
            delay(interval)
            assertContentEquals(stripPainter.buffer, outputs[0].pixels)
            assertContentEquals(stripPainter.buffer, outputs[1].pixels)

            // test transparent overlay
            content1.pixels = Array(pixelCount) {
                if (it % 2 == 0) Color.BLUE else EmptyColor
            }
            delay(interval)
            assertEquals(Color.BLUE, outputs[0].pixels?.get(0)) // content 1 color
            assertEquals(Color.RED, outputs[0].pixels?.get(1)) // content 2 color

            // stop player
            player.stop()
            delay(10)
            assertFalse(player.isRunning())
            assertFalse(content1.isEnabled)
            assertFalse(content2.isEnabled)
        }

        player.removeContent(content1)
        player.removeContent(content2)
        assertEquals(0, player.getPlaybackContents().size)
    }

    internal class TestPlaybackContent(
        override val playbackPriority: Int,
        private val id: Int
    ) : PlaybackContent {
        var pixels: Array<out ColorBase>? = null
        var isEnabled: Boolean = false

        override fun hasContent(): Boolean = pixels != null

        override fun produce(): Array<out ColorBase>? {
            return pixels
        }

        override fun onPlayerEnable(playbackScope: CoroutineScope) {
            isEnabled = true
        }

        override fun onPlayerDisable() {
            isEnabled = false
        }

        override fun getDescription(): String = "Test Content #$id"
    }

    internal class DummyOutput(id: String) : Output(createOutputConfig(id)) {

        companion object {
            fun createOutputConfig(id: String): OutputConfig {
                return OutputConfig(BaseConfigTest.TestPropertyProvider(0), "test_output", id)
            }
        }

        var pixels: Array<Color>? = null

        override fun onActivate(): OutputStatus {
            return OutputStatus.Connected
        }

        override fun onDeactivate(): OutputStatus {
            return OutputStatus.Disconnected
        }

        override fun onOutputPixels(pixels: Array<Color>) {
            this.pixels = pixels
        }
    }

}