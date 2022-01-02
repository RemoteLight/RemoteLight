package io.github.remotelight.core.config

interface PropertyHolder {

    fun <T : Any?> getProperty(id: String, type: Class<T>): T?

    fun <T : Any?> storeProperty(id: String, value: T): T

}