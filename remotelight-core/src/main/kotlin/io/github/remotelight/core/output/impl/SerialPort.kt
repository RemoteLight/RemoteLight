package io.github.remotelight.core.output.impl

import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortIOException
import org.tinylog.kotlin.Logger

class SerialPort(
    val baudRate: Int,
    val portDescriptor: String
) {

    companion object {
        fun getSerialPorts() = SerialPort.getCommPorts()

        fun getSerialPort(portDescriptor: String) = SerialPort.getCommPort(portDescriptor)

        fun existsSerialPort(portDescriptor: String) = getSerialPorts().any {
            it.portDescription == portDescriptor
        }
    }

    private var serialPort: SerialPort? = null

    val isOpen: Boolean
        get() = serialPort?.isOpen == true

    @Synchronized
    fun openSerialPort() {
        if (serialPort?.isOpen == true) {
            throw IllegalStateException("The serial port is already open. Close it before re-opening.")
        }

        val serialPort = getSerialPort(portDescriptor)
            ?: throw IllegalArgumentException("The specified serial port '$portDescriptor' is not available.")

        val openSuccessfully = serialPort.openPort(0)
        if (!openSuccessfully) {
            throw SerialPortIOException("Could not open serial port ${serialPort.portDescription}.")
        }

        this.serialPort = serialPort
        serialPort.baudRate = baudRate
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 1000, 0)
    }

    @Synchronized
    fun closeSerialPort() {
        val closeSuccessfully = serialPort?.closePort() ?: return
        serialPort = null

        if (!closeSuccessfully) {
            throw SerialPortIOException("Could not close serial port $portDescriptor.")
        }
        Logger.info("Closed serial port '$portDescriptor'.")
    }

    fun write(outputBuffer: ByteArray, size: Long) {
        serialPort?.writeBytes(outputBuffer, size)
    }

}