package io.github.remotelight.core.output.impl

import io.github.remotelight.core.color.Color
import io.github.remotelight.core.config.property.property
import io.github.remotelight.core.output.Output
import io.github.remotelight.core.output.OutputStatus
import io.github.remotelight.core.output.OutputVerification
import io.github.remotelight.core.output.config.OutputConfig
import io.github.remotelight.core.output.protocol.PixelProtocol
import io.github.remotelight.core.output.protocol.PixelProtocolSpecs
import org.tinylog.kotlin.Logger

class SerialOutput(
    outputConfig: OutputConfig,
    private val protocol: PixelProtocol
) : Output(outputConfig) {

    internal var serialPort: SerialPort? = null

    var baudRate by property("baud_rate", 1_000_000)

    var portDescriptor by property<String?>("port_descriptor", null)

    init {
        outputConfig.observeProperty<Int>("baud_rate") { newValue ->
            serialPort?.updateBaudRate(newValue)
        }
    }

    override fun onVerify(): OutputVerification {
        return when {
            portDescriptor.isNullOrBlank() -> OutputVerification.MissingProperty("port_descriptor")
            !SerialPort.existsSerialPort(
                portDescriptor ?: ""
            ) -> OutputVerification.NotAvailable("Serial Port $portDescriptor not available.")
            else -> OutputVerification.Ok
        }
    }

    override fun onActivate(): OutputStatus {
        if (serialPort?.isOpen == true) return OutputStatus.Connected

        val portDescriptor = portDescriptor ?: return OutputStatus.NotAvailable("Serial port descriptor not set.")
        val serialPort = SerialPort(portDescriptor)
        this.serialPort = serialPort

        return try {
            status = OutputStatus.Connecting
            serialPort.openSerialPort(baudRate)
            OutputStatus.Connected
        } catch (e: Exception) {
            Logger.error(e, "Could not open serial port $portDescriptor.")
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

    override fun onOutputPixels(pixels: Array<Color>) {
        val specs = PixelProtocolSpecs(config.colorOrder.isWhiteSupported && protocol.supportsRGBW())
        val outputBuffer = protocol.processPixels(pixels, specs)
        serialPort?.write(outputBuffer, outputBuffer.size.toLong())
    }

}