package io.github.remotelight.core.propeties

import com.beust.klaxon.Json
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

typealias Observer = (Any?) -> Unit

data class Property<T>(val id: String, private var _data: T): ReadWriteProperty<Config, T> {

    /**
     * Observers added to this list will be notified about data changes.
     */
    @Json(ignored = true)
    val dataObservers = mutableListOf<Observer>()

    var data: T by Delegates.observable(_data) { _, _, new ->
        _data = new
        dataObservers.forEach { it(new) }
    }

    operator fun provideDelegate(thisRef: Config, property: KProperty<*>): ReadWriteProperty<Config, T> {
        var prop = thisRef.getProperty<T>(id)
        if(prop == null) {
            prop = thisRef.addProperty(this)
        }
        return prop
    }

    override fun getValue(thisRef: Config, property: KProperty<*>): T {
        return thisRef.getProperty<T>(id)?.data?: data
    }

    override fun setValue(thisRef: Config, property: KProperty<*>, value: T) {
        val prop = thisRef.getProperty<T>(id)
        if(prop != null) {
            prop.data = value
        } else {
            this.data = value
            thisRef.addProperty(this)
        }
    }

}