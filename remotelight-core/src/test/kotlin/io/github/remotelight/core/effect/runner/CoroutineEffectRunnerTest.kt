package io.github.remotelight.core.effect.runner

import io.github.remotelight.core.config.BaseConfigTest
import io.github.remotelight.core.effect.*
import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class CoroutineEffectRunnerTest {

    @Test
    fun effectExecution() {
        val superVisorJob = SupervisorJob()
        val delay = 30L
        val pixels = 300
        val effectRunner = CoroutineEffectRunner(delay)
        val testEffect = TestEffect(createTestEffectConfig())

        assertFalse(effectRunner.isRunning())
        assertFalse(testEffect.onEnableCalled)
        val runnerTask = EffectRunnerTask(testEffect, pixels) { StripPainter(pixels) }
        runBlocking {
            assertThrows<IllegalStateException> { effectRunner.start(runnerTask) }
            effectRunner.onPlayerEnable(CoroutineScope(Dispatchers.Default + superVisorJob))

            effectRunner.start(runnerTask)
            delay(50)
            assertTrue(effectRunner.isRunning())
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