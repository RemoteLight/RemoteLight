package io.github.remotelight.core.config.property

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

internal class ListPropertyTest : BasePropertyTest() {

    @Test
    fun listProperty() {
        assertThrows<IllegalArgumentException> { ListProperty("list", "0", "1", "2", "3", "4", "5") }
        val listProp = ListProperty("list", "3", "1", "2", "3", "4", "5")
        assertEquals(5, listProp.size)
        var listValue by listProp.withConfig(testConfig)
        assertEquals("3", listValue)

        listProp.remove("5")
        assertFalse(listProp.contains("5"))
        assertNotEquals("5", listValue)

        listValue = "4"
        assertEquals("4", listProp.getValue(testConfig))
        listValue = "5"
        assertEquals("4", listProp.getValue(testConfig))
    }

}