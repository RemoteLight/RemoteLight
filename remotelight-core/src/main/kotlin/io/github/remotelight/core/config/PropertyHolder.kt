package io.github.remotelight.core.config

interface PropertyHolder {

    fun getProperty(id: String): Any?

    fun <T : Any?> storeProperty(id: String, value: T): T

}