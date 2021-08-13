package io.github.remotelight.core.propeties

import com.beust.klaxon.Json
import kotlin.properties.Delegates

typealias Observer = (Any?) -> Unit

data class Property(val id: String, private var _data: Any? = null) {

    /**
     * Observers added to this list will be notified about data changes.
     */
    @Json(ignored = true)
    val dataObservers = mutableListOf<Observer>()

    var data: Any? by Delegates.observable(_data) { _, _, new ->
        _data = new
        dataObservers.forEach { it(new) }
    }

}