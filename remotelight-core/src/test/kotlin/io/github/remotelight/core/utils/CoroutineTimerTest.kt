package io.github.remotelight.core.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertTimeout
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

internal class CoroutineTimerTest {

    @Test
    fun testTimer() {
        val interval = 30.milliseconds
        val timer = CoroutineTimer(interval, 8.0)
        assertFalse(timer.isRunning())

        runBlocking {
            var counter = 0L
            timer.start(this) {
                counter++
            }
            assertTrue(timer.isRunning())

            delay(10)
            assertEquals(1, counter)
            delay(interval)
            assertEquals(2, counter)

            timer.stop()
            delay(interval)
            assertFalse(timer.isRunning())
            assertEquals(2, counter)

            timer.start(this) {
                counter++
            }
            delay(10)
            assertTrue(timer.isRunning())
            assertEquals(3, counter)
            delay(interval)
            assertEquals(4, counter)

            assertTimeout(interval.toJavaDuration()) {
                runBlocking {
                    timer.stopAndJoin()
                }
            }
        }
    }

}