package io.github.remotelight.core.output.impl

import io.github.remotelight.core.color.Color
import io.github.remotelight.core.output.OutputStatus
import io.github.remotelight.core.output.protocol.GlediatorProtocol
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class SerialOutputTest : BaseOutputTest() {

    private fun assumeSerialPortDescriptor(): String {
        Assumptions.assumeFalse(SerialPort.getSerialPorts().isEmpty(), "No serial port available.")
        val portDescriptor = SerialPort.getSerialPorts()[0].systemPortName
        println("Using serial port for the test: $portDescriptor")
        return portDescriptor
    }

    @Test
    fun activateDeactivate() {
        val portDescriptor = assumeSerialPortDescriptor()
        val serialOutput = SerialOutput(testOutputConfig(), GlediatorProtocol)
        assertEquals(OutputStatus.NotAvailable::class, serialOutput.activate()::class)
        assertEquals(OutputStatus.NotAvailable::class, serialOutput.status::class)

        serialOutput.portDescriptor = portDescriptor
        assertEquals(OutputStatus.Connected, serialOutput.activate())
        assertEquals(OutputStatus.Connected, serialOutput.status)
        assertNotNull(serialOutput.serialPort)
        assertTrue(serialOutput.serialPort?.isOpen == true)
        assertEquals(OutputStatus.Connected, serialOutput.activate()) // re-activate output

        serialOutput.baudRate = 9600
        assertEquals(9600, serialOutput.baudRate)
        assertEquals(9600, serialOutput.serialPort?.getBaudRate())

        assertEquals(OutputStatus.Disconnected, serialOutput.deactivate())
        assertEquals(OutputStatus.Disconnected, serialOutput.status)
        assertNull(serialOutput.serialPort)
        assertEquals(OutputStatus.Disconnected, serialOutput.deactivate()) // re-deactivate output

        // activate and deactivate again
        assertEquals(OutputStatus.Connected, serialOutput.activate())
        assertEquals(OutputStatus.Disconnected, serialOutput.deactivate())
    }

    @Test
    fun pixelOutput() {
        val pixelCount = 60
        val delay = 10L
        val serialOutput = SerialOutput(testOutputConfig(pixelCount), GlediatorProtocol)
        serialOutput.portDescriptor = assumeSerialPortDescriptor()
        assertEquals(OutputStatus.Connected, serialOutput.activate())

        println("Fading to RED...")
        for (i in 0..255) {
            serialOutput.outputPixels(generatePixelArray(pixelCount, Color(i, 0, 0)))
            Thread.sleep(delay)
        }

        println("Fading to GREEN...")
        for (i in 0..255) {
            serialOutput.outputPixels(generatePixelArray(pixelCount, Color(0, i, 0)))
            Thread.sleep(delay)
        }

        println("Fading to BLUE...")
        for (i in 0..255) {
            serialOutput.outputPixels(generatePixelArray(pixelCount, Color(0, 0, i)))
            Thread.sleep(delay)
        }

        println("White Scanner...")
        for (i in 0 until pixelCount) {
            val pixels = generatePixelArray(pixelCount, Color.BLACK)
            pixels[i] = Color(255, 255, 255)
            serialOutput.outputPixels(pixels)
            Thread.sleep(delay)
        }
        for (i in pixelCount-1 downTo 0) {
            val pixels = generatePixelArray(pixelCount, Color.BLACK)
            pixels[i] = Color(255, 255, 255)
            serialOutput.outputPixels(pixels)
            Thread.sleep(delay)
        }

        println("Turning pixels off...")
        serialOutput.outputPixels(generatePixelArray(pixelCount, Color.BLACK))
        assertEquals(OutputStatus.Disconnected, serialOutput.deactivate())
    }

}