package io.github.remotelight.core.config

import com.beust.klaxon.Json
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

typealias Observer = (Any?) -> Unit

open class Property<T>(val id: String, data: T): ReadWriteProperty<Config, T> {

    /**
     * Observers added to this list will be notified about data changes.
     */
    @Json(ignored = true)
    val dataObservers = mutableListOf<Observer>()

    var data: T by Delegates.observable(data) { _, _, new ->
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