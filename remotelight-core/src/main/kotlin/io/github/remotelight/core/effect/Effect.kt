package io.github.remotelight.core.effect

import io.github.remotelight.core.config.PropertyHolder
import io.github.remotelight.core.utils.reactive.ObserverList
import kotlin.properties.Delegates

abstract class Effect(val config: EffectConfig) : PropertyHolder {

    val observableStatus = ObserverList<EffectStatus>()
    open var status: EffectStatus by Delegates.observable(EffectStatus.Stopped) { _, oldValue, newValue ->
        observableStatus.notify(oldValue, newValue)
    }

    open fun onEnable(pixels: Int) {}

    abstract fun doEffect(strip: StripPainter)

    open fun onDisable() {}

    override fun <T> getProperty(id: String, type: Class<T>) = config.getProperty(id, type)

    override fun <T> storeProperty(id: String, value: T): T = config.storeProperty(id, value)

}