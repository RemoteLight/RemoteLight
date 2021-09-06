package io.github.remotelight.core.output

import io.github.remotelight.core.color.Color
import io.github.remotelight.core.function.Status
import io.github.remotelight.core.utils.reactive.ObserverList
import kotlin.properties.Delegates

abstract class Output(val config: OutputConfig) {

    val observableStatus = ObserverList<Status>()
    open var status: Status by Delegates.observable(Status.Stopped) { _, oldValue, newValue ->
        observableStatus.notify(oldValue, newValue)
    }

    abstract fun activate()

    abstract fun deactivate()

    abstract fun getPixelCount(): Int

    abstract fun outputPixels(pixels: Array<Color>)

}