package io.github.remotelight.core.config

interface PropertyHolder {

    /**
     * Specifies whether properties should be saved by default when defined,
     * or only when the value is changed.
     */
    val storeDefaultValue: Boolean

    fun <T : Any?> getProperty(id: String, type: Class<T>): T?

    fun <T : Any?> storeProperty(id: String, value: T): T

}