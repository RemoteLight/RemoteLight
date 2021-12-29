package io.github.remotelight.core.output

import io.github.remotelight.core.color.Color
import io.github.remotelight.core.config.PropertyHolder
import io.github.remotelight.core.output.config.OutputConfig
import io.github.remotelight.core.utils.reactive.ObserverList
import kotlin.properties.Delegates

abstract class Output(val config: OutputConfig) : PropertyHolder {

    val observableStatus = ObserverList<OutputStatus>()
    open var status: OutputStatus by Delegates.observable(OutputStatus.Disconnected) { _, oldValue, newValue ->
        observableStatus.notify(oldValue, newValue)
    }

    protected abstract fun onActivate(): OutputStatus

    protected abstract fun onDeactivate(): OutputStatus

    fun activate() {
        status = onActivate()
    }

    fun deactivate() {
        status = onDeactivate()
    }

    open fun getPixelCount(): Int = config.pixels

    abstract fun outputPixels(pixels: Array<Color>)

    override fun getProperty(id: String) = config.getProperty(id)

    override fun <T> storeProperty(id: String, value: T): T = config.storeProperty(id, value)

}