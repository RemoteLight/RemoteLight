package io.github.remotelight.core.color

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ColorTest {

    @Test
    fun colorTest() {
        var color = Color(255, 100, 10)
        assertEquals(255, color.red)
        assertEquals(100, color.green)
        assertEquals(10, color.blue)
        assertEquals(0, color.white)

        color = Color(300, -10, 0)
        assertEquals(255, color.red)
        assertEquals(0, color.green)

        color = Color(177, 25, 3, 200)
        assertEquals(200, color.white)
        assertEquals(color, color.copy())
    }

}