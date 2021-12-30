package io.github.remotelight.core.output

import io.github.remotelight.core.color.Color
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse

internal class ColorOrderTest {

    @Test
    fun verifyNoDuplicates() {
        assertFalse(hasDuplicates(Color(255, 200, 100), false))
        assertFalse(hasDuplicates(Color(10, 20, 30, 40), true))
    }

    private fun hasDuplicates(testColor: Color, includeWhiteChannel: Boolean) : Boolean {
        val colorMap = ColorOrder.values()
            .filter { it.isWhiteSupported == includeWhiteChannel }
            .map { testColor.applyColorOrder(it) }
        return colorMap.distinct().size != colorMap.size
    }

}