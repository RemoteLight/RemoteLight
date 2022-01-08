package io.github.remotelight.core.config.provider

interface PropertyProvider<R> {

    fun onInit()

    fun onClose()

    fun storeProperties()

    fun getProperties(): Map<String, *>

    fun getRawProperties(): Map<String, R>

    fun <T : Any?> getProperty(id: String, type: Class<T>): T

    fun hasProperty(id: String): Boolean

    fun <T> setProperty(id: String, value: T)

    fun setRawProperty(id: String, value: R)

    fun deleteProperty(id: String): Any?

    fun clear()

}