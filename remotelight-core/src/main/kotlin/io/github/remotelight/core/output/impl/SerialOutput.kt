package io.github.remotelight.core.output.impl

import io.github.remotelight.core.color.Color
import io.github.remotelight.core.config.property.Property
import io.github.remotelight.core.output.Output
import io.github.remotelight.core.output.OutputStatus
import io.github.remotelight.core.output.config.OutputConfig
import io.github.remotelight.core.output.protocol.PixelProtocol

class SerialOutput(
    outputConfig: OutputConfig,
    private val protocol: PixelProtocol
) : Output(outputConfig) {

    private var serialPort: SerialPort? = null

    var baudRate by Property("baud_rate", 1_000_000)

    var portDescriptor by Property<String?>("port_descriptor", null)

    override fun onActivate(): OutputStatus {
        if (serialPort?.isOpen == true) return OutputStatus.Connected

        val portDescriptor = portDescriptor ?: return OutputStatus.NotAvailable("Serial port descriptor not set.")
        val serialPort = SerialPort(
            baudRate,
            portDescriptor
        )
        this.serialPort = serialPort

        return try {
            status = OutputStatus.Connecting
            serialPort.openSerialPort()
            OutputStatus.Connected
        } catch (e: Exception) {
            OutputStatus.Failed(e)
        }
    }

    override fun onDeactivate(): OutputStatus {
        if (serialPort == null || serialPort?.isOpen == false) return OutputStatus.Disconnected

        return try {
            status = OutputStatus.Disconnecting
            serialPort?.closeSerialPort()
            serialPort = null
            OutputStatus.Disconnected
        } catch (e: Exception) {
            OutputStatus.Failed(e)
        }
    }

    override fun outputPixels(pixels: Array<Color>) {
        val outputBuffer = protocol.processPixels(pixels)
        serialPort?.write(outputBuffer, outputBuffer.size.toLong())
    }

}