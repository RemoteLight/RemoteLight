package io.github.remotelight.core.output

import io.github.remotelight.core.color.Color
import io.github.remotelight.core.color.GammaCorrection
import io.github.remotelight.core.config.PropertyHolder
import io.github.remotelight.core.output.config.OutputConfig
import io.github.remotelight.core.utils.reactive.ObserverList
import kotlin.properties.Delegates

abstract class Output(val config: OutputConfig) : PropertyHolder {

    override val storeDefaultValue get() = config.storeDefaultValue

    val observableStatus = ObserverList<OutputStatus>()
    open var status: OutputStatus by Delegates.observable(OutputStatus.Disconnected) { _, oldValue, newValue ->
        observableStatus.notify(oldValue, newValue)
    }

    protected abstract fun onActivate(): OutputStatus

    protected abstract fun onDeactivate(): OutputStatus

    fun activate(): OutputStatus {
        return onActivate().also { status = it }
    }

    fun deactivate(): OutputStatus {
        return onDeactivate().also { status = it }
    }

    fun verify(): OutputVerification {
        return when {
            config.pixels <= 0 -> OutputVerification.MissingProperty("pixels")
            else -> onVerify()
        }
    }

    protected open fun onVerify(): OutputVerification = OutputVerification.Ok

    fun outputPixels(pixels: Array<Color>) {
        if (config.gammaCorrectionEnabled) {
            pixels.forEachIndexed { index, color ->
                pixels[index] = GammaCorrection.applyGammaCorrection(color)
            }
        }
        val colorOrdered = pixels.map { it.applyColorOrder(config.colorOrder) }
        onOutputPixels(colorOrdered.toTypedArray())
    }

    protected abstract fun onOutputPixels(pixels: Array<Color>)

    override fun <T> getProperty(id: String, type: Class<T>) = config.getProperty(id, type)

    override fun <T> storeProperty(id: String, value: T): T = config.storeProperty(id, value)

}