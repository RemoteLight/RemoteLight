package io.github.remotelight.core.effect.runner

import io.github.remotelight.core.config.BaseConfigTest
import io.github.remotelight.core.effect.*
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

internal class CoroutineEffectRunnerTest {

    @Test
    fun effectExecution() {
        val superVisorJob = SupervisorJob()
        val delay = 30L
        val pixels = 300
        val effectRunner = CoroutineEffectRunner(superVisorJob, delay)
        val testEffect = TestEffect(createTestEffectConfig())

        assertFalse(effectRunner.isRunning())
        assertFalse(testEffect.onEnableCalled)
        val runnerTask = EffectRunnerTask(testEffect, pixels) { StripPainter(pixels) }
        runBlocking {
            effectRunner.start(runnerTask)
            assertTrue(effectRunner.isRunning())
            delay(10)
            assertTrue(testEffect.onEnableCalled)
            assertFalse(testEffect.onDisableCalled)

            delay(500)
            val tolerance = 10
            assertTrue(LongRange(delay - tolerance, delay + tolerance).contains(testEffect.avgDelay))

            // stop effect runner
            effectRunner.stop()
            delay(10)
            assertFalse(effectRunner.isRunning())
            assertTrue(testEffect.onDisableCalled)

            // cancel parent job
            effectRunner.start(runnerTask)
            assertTrue(effectRunner.isRunning())
            delay(delay * 2)
            superVisorJob.cancel()
            delay(delay)
            assertFalse(effectRunner.isRunning())
        }
    }

    @Test
    fun fpsReport() {
        val delay = 30L
        val effectRunner = CoroutineEffectRunner(SupervisorJob(), delay)
        val testEffect = TestEffect(createTestEffectConfig())
        val runnerTask = EffectRunnerTask(testEffect, 60) { StripPainter(it.pixels) }

        runBlocking {
            var lastReportedFPS = -1.0
            val collectJob = launch {
                effectRunner.framesPerSecond.collect {
                    lastReportedFPS = it
                }
            }
            assertEquals(-1.0, lastReportedFPS)
            effectRunner.start(runnerTask)
            delay(3000)
            assertNotEquals(-1.0, lastReportedFPS)
            collectJob.cancel()
        }
    }

    private fun createTestEffectConfig() = EffectConfig(
        BaseConfigTest.TestPropertyProvider(0),
        EffectIdentifier("test_effect", EffectCategory.Animation)
    )

    internal class TestEffect(config: EffectConfig) : Effect(config) {
        var avgDelay = 0L
        private var lastExecution = -1L
        var onEnableCalled = false
        var onDisableCalled = false

        override fun doEffect(strip: StripPainter) {
            avgDelay = if (lastExecution != -1L) {
                (avgDelay + (System.currentTimeMillis() - lastExecution)) / 2
            } else {
                0
            }
            lastExecution = System.currentTimeMillis()
        }

        override fun onEnable(pixels: Int) {
            onEnableCalled = true
        }

        override fun onDisable() {
            onDisableCalled = true
        }

    }

}