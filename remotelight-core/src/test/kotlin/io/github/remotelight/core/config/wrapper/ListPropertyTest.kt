package io.github.remotelight.core.config.wrapper

import io.github.remotelight.core.config.Property
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

internal class ListPropertyTest {

    @Test
    fun listProperty() {
        val property = Property("test", "5")
        val listProp = ListProperty(property, "1", "2", "3", "4", "5")
        assertEquals(property, listProp.property)
        assertEquals(property.data, listProp.data)
        assertEquals(5, listProp.size())

        listProp.remove("5")
        assertFalse(listProp.contains("5"))
        assertNotEquals("5", property.data)

        property.data = "4"
        assertEquals("4", listProp.data)
        listProp.data = "5"
        assertEquals("4", property.data)
    }

}