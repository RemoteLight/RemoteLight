package io.github.remotelight.core.config

import com.beust.klaxon.Json
import io.github.remotelight.core.utils.reactive.Observer
import io.github.remotelight.core.utils.reactive.ObserverList
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Observe the data of the property by getting the old and new data value.
 */
fun <T> Property<T>.observeBoth(observer: Observer<T>) = this.dataObservers.observe(observer)

/**
 * Observe the data of the property by getting the new data value.
 */
fun <T> Property<T>.observe(observer: (T) -> Unit) = this.dataObservers.observe(observer)

open class Property<T>(val id: String, data: T): ReadWriteProperty<Config, T> {

    /**
     * Observers added to this list will be notified about data changes.
     */
    @Json(ignored = true)
    val dataObservers = ObserverList<T>()

    var data: T by Delegates.observable(data) { _, old, new ->
        dataObservers.notify(old, new)
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Property<*>

        if (id != other.id) return false
        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (data?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Property(id='$id', data=$data, dataObservers=$dataObservers)"
    }


}