package io.github.remotelight.core.config

interface ConfigChangeCallback {

    fun onConfigChange(properties: Map<String, Any?>)

}