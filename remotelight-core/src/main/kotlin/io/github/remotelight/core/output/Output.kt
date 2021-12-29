package io.github.remotelight.core.output

import io.github.remotelight.core.color.Color
import io.github.remotelight.core.config.PropertyHolder
import io.github.remotelight.core.function.Status
import io.github.remotelight.core.output.config.OutputConfig
import io.github.remotelight.core.utils.reactive.ObserverList
import kotlin.properties.Delegates

abstract class Output(val config: OutputConfig) : PropertyHolder {

    val observableStatus = ObserverList<Status>()
    open var status: Status by Delegates.observable(Status.Stopped) { _, oldValue, newValue ->
        observableStatus.notify(oldValue, newValue)
    }

    abstract fun activate()

    abstract fun deactivate()

    abstract fun getPixelCount(): Int

    abstract fun outputPixels(pixels: Array<Color>)

    override fun getProperty(id: String) = config.getProperty(id)

    override fun <T> storeProperty(id: String, value: T): T = config.storeProperty(id, value)
}